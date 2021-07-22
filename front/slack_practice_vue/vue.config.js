'use strict'
const path = require('path')
module.exports = {
  outputDir: path.resolve(__dirname, '../../src/main/resources/static/vue'),
  indexPath: path.resolve(__dirname, '../../src/main/resources/templates/index.mustache'),

  devServer: {
    overlay: false, // 웹팩 데브에서 제공하는 에러 화면 나타내 주는 기능을 끄는 것
    host: 'localhost',
    port: 8080,
  },
}
