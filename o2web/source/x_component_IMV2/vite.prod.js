import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { readFileSync, existsSync, statSync, readdirSync } from "fs"
import path from "node:path";
import fs from "fs/promises";
import UglifyJS from "uglify-js";

const pkgPath = path.resolve(process.cwd(), 'package.json');
const pkgJson = JSON.parse(readFileSync(pkgPath));
const componentPath = pkgJson.name;
const componentName = componentPath.replace('x_component_', '').split('_').join('.');

function getOutDir(){
  switch (process.env.npm_lifecycle_event){
    case 'o2-build':
      return `../../../target/o2server/servers/webServer/${componentPath}`
    case 'o2-deploy':
      return `../../dest/${componentPath}`;
    default:
      return `dist/${componentPath}`
  }
}
const outDir = getOutDir();

const viteConfig = {
  define: {
    'process.env': {
      NODE_ENV: JSON.stringify(process.env.NODE_ENV || 'development'),
    }
  },
  build: {
    emitAssets: true,
    assetsDir: '$Main',
    outDir: outDir,
    sourcemap: true,
    lib: {
      entry: ['src/main.js'],
      name: `${componentName}`,
      formats: ['umd'],
      fileName: (format, entryName) => {
        return `$Main/js/${entryName}.js`;
      },
      // cssFileName: '$Main/css/style.css',
    }
  }
}

  function getAllFiles(dir, files_ = [], nest=false) {
    const files = readdirSync(dir);
    for (const i in files) {
      const name = path.join(dir, files[i]);
      if( existsSync(name) ){
        if (statSync(name).isDirectory()) {
          if(nest)getAllFiles(name, files_, nest);
        } else {
          files_.push(name);
        }
      }
    }
    return files_;
  }

  function includeMain(relativePath, extname, nest){
    const dir = path.resolve(`${process.cwd()}`, `${outDir}/${relativePath}/`);
    if (existsSync(dir)) {
      return getAllFiles(dir, [], nest).filter(file =>{
        return path.extname(file) === extname;
      }).map(file =>{
        return `'../${componentPath}/${relativePath}${file.replace(dir, '').replace(/\\/g, '/')}'`
            .replace(/\/\//g, '/');
      });
    }else{
      return []
    }
  }

  function findLp(relativePath, extname, nest){
    const dir = path.resolve(`${process.cwd()}`, `${outDir}/${relativePath}/`);
    if (existsSync(dir)) {
      return getAllFiles(dir, [], nest).filter(file =>{
        return path.extname(file) === extname;
      }).map(filePath =>{
        return filePath;
      });
    }else{
      return []
    }
  }

  function generateMiniLP(){
    const lps = findLp('lp', '.js', false);
    lps.forEach(lpPath => {
      const o = path.parse(lpPath);

      const toPath = path.resolve(`${o.dir}`, `${o.name}.min${o.ext}`);
      fs.readFile(lpPath).then((data)=>{
        const miniContent = UglifyJS.minify(data.toString()).code;
        fs.writeFile(toPath, miniContent, 'utf8').catch((err) => {
          console.error('Failed to write file lp.min.js:', err);
        })
      }, (err) => {
        console.error('Failed to read file lp.min.js:', err);
      });
    })
  }

  function generateMain(){
    let mainFileContent = `o2.component("${componentName}", {\n`;

    const maincsss = includeMain('$Main/css', '.css', false);
    const csss = includeMain('', '.css', false).concat(maincsss);
    if (csss.length) mainFileContent += `    css: [${csss.join(", ")}],\n`;

    const jss = includeMain('$Main/js', '.js', false);
    if (jss.length) mainFileContent += `    js: [${jss.join(", ")}],\n`;

    mainFileContent += `});`;

    const filePath = path.resolve( `${process.cwd()}`, outDir, `Main.js`);
    fs.writeFile(filePath, mainFileContent, 'utf8').catch((err) => {
      console.error('Failed to generate Main.js:', err);
    });

    const miniFileContent = UglifyJS.minify(mainFileContent).code;
    const minFilePath = path.resolve(`${process.cwd()}`, outDir, `Main.min.js`);
    fs.writeFile(minFilePath, miniFileContent, 'utf8').catch((err) => {
      console.error('Failed to generate Main.min.js:', err);
    });
  }

  function generateStarter() {
    const filePath = path.resolve( `${process.cwd()}`, outDir, `Starter.js`);
    fs.readFile(filePath).then((data)=>{
      const miniContent = UglifyJS.minify(data.toString()).code;
      const minFilePath = path.resolve(`${process.cwd()}`, outDir, `Starter.min.js`);
      fs.writeFile(minFilePath, miniContent, 'utf8').catch((err) => {
        console.error('Failed to generate Starter.min.js:', err);
      });
    })
  }

  function generateMainPlugin() {
    return {
      name: 'generate-file',
      closeBundle() {
        generateStarter();
        generateMain();
        generateMiniLP();

      },
    };
  }


viteConfig.plugins = [
  vue({
    template: {
      compilerOptions: {
        isCustomElement: (tag) => ['oo-input', 'oo-button', 'oo-switch'].includes(tag),
      }
    }
  }),
  generateMainPlugin(),
];

// https://vite.dev/config/
export default defineConfig(viteConfig)
