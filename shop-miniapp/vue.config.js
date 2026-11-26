module.exports = {
  // uni-app Vue2 项目配置
}
const fs = require('fs')
const path = require('path')

// HBuilderX 编译器不会自动读取本地开发环境文件，显式注入小程序 API 地址。
if (process.env.NODE_ENV !== 'production' && !process.env.VUE_APP_DEV_API_BASE_URL) {
  const localEnvFile = path.resolve(__dirname, '.env.development.local')
  if (fs.existsSync(localEnvFile)) {
    const match = fs.readFileSync(localEnvFile, 'utf8').match(/^VUE_APP_DEV_API_BASE_URL\s*=\s*(.+)$/m)
    if (match) {
      process.env.VUE_APP_DEV_API_BASE_URL = match[1].trim().replace(/^['"]|['"]$/g, '')
    }
  }
}

function copyDirectory(sourceDir, targetDir) {
  if (!fs.existsSync(sourceDir)) {
    return
  }

  fs.mkdirSync(targetDir, { recursive: true })

  fs.readdirSync(sourceDir, { withFileTypes: true }).forEach(entry => {
    const sourcePath = path.join(sourceDir, entry.name)
    const targetPath = path.join(targetDir, entry.name)

    if (entry.isDirectory()) {
      copyDirectory(sourcePath, targetPath)
      return
    }

    fs.copyFileSync(sourcePath, targetPath)
  })
}

function copyFile(sourceFile, targetFile) {
  if (!fs.existsSync(sourceFile)) {
    return
  }

  fs.mkdirSync(path.dirname(targetFile), { recursive: true })
  fs.copyFileSync(sourceFile, targetFile)
}

class CopySkillsPlugin {
  apply(compiler) {
    compiler.hooks.afterEmit.tap('CopySkillsPlugin', () => {
      const outputDir = compiler.options.output.path
      copyDirectory(path.resolve(__dirname, 'skills'), path.join(outputDir, 'skills'))
      copyFile(path.resolve(__dirname, 'agent/AGENTS.md'), path.join(outputDir, 'agent/AGENTS.md'))
      copyFile(path.resolve(__dirname, 'agent/page-meta.json'), path.join(outputDir, 'agent/page-meta.json'))
    })
  }
}

module.exports = {
  configureWebpack: () => {
    if (process.env.UNI_PLATFORM !== 'mp-weixin') {
      return {}
    }

    return {
      plugins: [new CopySkillsPlugin()]
    }
  }
}
