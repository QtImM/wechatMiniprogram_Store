"""在客户 Linux 主机部署本目录的已审核版本，任何阶段失败即停止。"""
import argparse
import json
import os
from pathlib import Path
import subprocess
import time
from urllib.request import urlopen
from delivery_backup import ROOT, compose
from release_preflight import read_env, validate


def checked_python(script, *arguments, env=None):
    import sys
    subprocess.run([sys.executable, str(ROOT / 'scripts' / script), *map(str, arguments)],
                   check=True, env=env)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('action', choices=['initialize', 'upgrade'])
    parser.add_argument('--env', required=True, type=Path)
    parser.add_argument('--backup', type=Path, help='升级前成套备份文件，必须为新的路径')
    args = parser.parse_args()
    if subprocess.check_output(['git', 'status', '--porcelain'], cwd=ROOT).strip():
        raise ValueError('源码包含未提交修改，请先完成审查并固定版本')
    values = read_env(args.env)
    errors = validate(values)
    if errors:
        raise ValueError('预检失败：' + '; '.join(errors))
    for name in ['fullchain.pem', 'privkey.pem']:
        if not (ROOT / 'deploy/secrets/tls' / name).is_file():
            raise ValueError('缺少 HTTPS 证书文件：' + name)
    for name in ['material', 'mysql', 'redis']:
        (ROOT / 'deploy/data' / name).mkdir(parents=True, exist_ok=True)
    database = values['MYSQL_DATABASE']
    bootstrap_env = os.environ.copy()
    bootstrap_env['DELIVERY_ADMIN_PASSWORD'] = values.get('DELIVERY_ADMIN_PASSWORD', '')
    if args.action == 'upgrade':
        if args.backup is None:
            raise ValueError('升级必须指定 --backup，不允许跳过备份')
        checked_python('delivery_backup.py', 'backup', '--env', args.env,
                       '--database', database, '--archive', args.backup)
    else:
        if len(bootstrap_env.get('DELIVERY_ADMIN_PASSWORD', '')) < 16:
            raise ValueError('首次部署须通过环境设置 DELIVERY_ADMIN_PASSWORD，至少 16 字符')
    # 配置输出可能包含密钥，只做静默校验，不打印展开后的配置。
    compose(args, 'config', '--quiet')
    compose(args, 'build')
    compose(args, 'up', '-d', '--wait', '--wait-timeout', '180', 'mysql', 'redis')
    compose(args, 'stop', 'backend')
    checked_python('prepare_database.py', args.action if args.action == 'initialize' else 'migrate',
                   '--env', args.env, '--database', database, env=bootstrap_env)
    compose(args, 'run', '--rm', '--no-deps', '--user', '0', '--entrypoint', 'sh', 'backend',
            '-c', 'chown -R 10001:10001 /app/material /app/logs')
    compose(args, 'up', '-d', '--wait', '--wait-timeout', '240')
    url = values['APP_EXTERNAL_BASE_URL'].rstrip('/') + '/readyz'
    with urlopen(url, timeout=10) as response:
        if response.status != 200 or json.load(response).get('status') != 'UP':
            raise ValueError('业务就绪检查未通过')
    manifest = {
        'commit': subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=ROOT).decode().strip(),
        'mode': values['APP_DELIVERY_MODE'], 'time': int(time.time()),
        'images': compose(args, 'images', '--format', 'json', stdout=subprocess.PIPE).stdout.decode(),
        'status': 'deployed-awaiting-business-acceptance'
    }
    destination = ROOT / 'deploy/releases' / ('release-%d.json' % manifest['time'])
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_text(json.dumps(manifest, ensure_ascii=False, indent=2), encoding='utf-8')
    print('部署与健康检查通过；请按验收清单完成业务验证。发布记录：', destination)


if __name__ == '__main__':
    main()
