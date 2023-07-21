import { defineConfig } from 'vite';

const host = 'http://172.16.91.6';
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
    base: '/x_init',
    server: {
        proxy: {
            '/jaxrs': o,
            '/x_program_center': o
        },
        sourcemap: true
    }
})
