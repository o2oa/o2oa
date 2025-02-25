import { defineConfig } from 'vite';
import devConfig from './vite.dev.js';
import prodConfig from './vite.prod.js';

export default ({ mode }) => {
  const config = mode === 'production' ? prodConfig : devConfig;
  return defineConfig(Object.assign(config, {
    //您的配置
  }));
};
