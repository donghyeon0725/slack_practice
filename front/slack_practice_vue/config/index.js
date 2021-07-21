'use strict'
const path = require('path')
module.exports = {
    dev: {
        // Paths
        assetsSubDirectory: 'static',
        assetsPublicPath: '/',
        proxyTable: {},

        // Various Dev Server settings
        host: 'localhost',
        overlay: false,
        port: 8080,

        // skipping other options as they are only convenience features
    },
    build: {

        // Template for index.html
        index: path.resolve(__dirname, '../../../src/main/resources/static/index.html'),

        // Paths
        assetsRoot: path.resolve(__dirname, '../../../src/main/resources/static'),
        assetsSubDirectory: 'static',
        assetsPublicPath: '/',
        productionSourceMap: true,

        // skipping the rest ...
    },
}
