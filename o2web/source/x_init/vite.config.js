import { defineConfig } from 'vite';

const host = 'http://172.16.1.59';
const o={
    target: host,
    changeOrigin: true,
    onProxyRes: (proxyRes, req, res) => {
        const cookie = proxyRes.headers['set-cookie'];
        if (cookie && cookie[0]){
            const host = req.headers['host'].split(':')[0];
            cookie[0] = cookie[0].replace(/(domain=)([^;]+)/, '$1'+host);
        }
    }
}
export default defineConfig({
    // assetsInclude: ['**/*.html'],
    server: {
        proxy: {
            '/jaxrs': o,
            '/x_program_center': o,
            '/x_desktop': o
        },
        host: true,
        sourcemap: true
    },
    base: './'
})
