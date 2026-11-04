"""临时 CI 中以真实浏览器核对录入后台，并生成标注为演练环境的教程截图。"""
import os
import re
from playwright.sync_api import sync_playwright, expect
from delivery_backup import ROOT
from release_preflight import read_env


def verify_browser():
    if os.environ.get('GITHUB_ACTIONS') != 'true':
        raise ValueError('仅允许临时 CI 环境执行')
    values = read_env(ROOT / '.env.prod')
    output = ROOT / 'deploy/reviews/screenshots'
    output.mkdir(parents=True, exist_ok=True)
    errors = []
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(args=['--no-proxy-server'])
        context = browser.new_context(ignore_https_errors=True, viewport={'width': 1440, 'height': 1000}, locale='zh-CN')
        page = context.new_page()
        page.on('pageerror', lambda error: errors.append(str(error)))
        page.set_default_timeout(20000)
        base = values['APP_EXTERNAL_BASE_URL']
        page.goto(base + '/#/login')
        expect(page.get_by_placeholder('账号', exact=True)).to_be_visible()
        page.screenshot(path=str(output / '01-登录-临时演练.png'), full_page=True)
        page.get_by_placeholder('账号', exact=True).fill('admin')
        page.get_by_placeholder('密码', exact=True).fill(values['DELIVERY_ADMIN_PASSWORD'])
        page.get_by_role('button', name=re.compile(r'登\s*录')).click()
        page.wait_for_url('**/#/product/spu')
        expect(page.get_by_role('button', name='新增商品', exact=True)).to_be_visible()
        expect(page.get_by_role('cell', name='交付演练商品', exact=True)).to_be_visible()
        page.screenshot(path=str(output / '02-商品列表-临时演练.png'), full_page=True)
        page.get_by_role('button', name='新增商品', exact=True).click()
        expect(page.get_by_placeholder('请输入商品名称')).to_be_visible()
        expect(page.locator('.el-form-item').filter(has_text='状态').get_by_text('下架', exact=True)).to_be_visible()
        page.screenshot(path=str(output / '03-新增商品默认下架-临时演练.png'), full_page=True)
        page.goto(base + '/#/system/shop')
        name = page.locator('.el-form-item').filter(has_text='店铺展示名').get_by_role('textbox')
        expect(name).to_have_value('陕铺演练店')
        name.fill('陕铺浏览器演练店')
        with page.expect_response(lambda response: '/admin-api/system/shop' in response.url and response.request.method == 'POST') as saved:
            page.get_by_role('button', name='保存设置', exact=True).click()
        if saved.value.status != 200 or saved.value.json().get('code') != 0:
            raise AssertionError('浏览器保存店铺展示设置失败')
        page.reload()
        expect(name).to_have_value('陕铺浏览器演练店')
        page.screenshot(path=str(output / '04-店铺展示设置-临时演练.png'), full_page=True)
        context.close()
        for username, can_create in [('delivery_operator', True), ('delivery_readonly', False)]:
            role_context = browser.new_context(ignore_https_errors=True, viewport={'width': 1440, 'height': 1000}, locale='zh-CN')
            role_page = role_context.new_page()
            role_page.on('pageerror', lambda error: errors.append(str(error)))
            role_page.goto(base + '/#/login')
            role_page.get_by_placeholder('账号', exact=True).fill(username)
            role_page.get_by_placeholder('密码', exact=True).fill(values['DELIVERY_ADMIN_PASSWORD'])
            role_page.get_by_role('button', name=re.compile(r'登\s*录')).click()
            role_page.wait_for_url('**/#/product/spu')
            expect(role_page.get_by_role('cell', name='交付演练商品', exact=True)).to_be_visible()
            create = role_page.get_by_role('button', name='新增商品', exact=True)
            expect(create).to_be_visible() if can_create else expect(create).to_have_count(0)
            role_page.screenshot(path=str(output / ('05-' + username + '-临时演练.png')), full_page=True)
            role_context.close()
        browser.close()
    if errors:
        raise AssertionError('浏览器发生脚本异常：' + '; '.join(errors))
    print('真实浏览器登录、商品列表、新增默认下架、展示配置保存与刷新持久化通过；截图来自临时演练环境。', flush=True)


if __name__ == '__main__':
    verify_browser()
