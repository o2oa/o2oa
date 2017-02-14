/* global require, module */

var path = require('path');

module.exports = function(grunt) {
    'use strict';

    // These plugins provide necessary tasks.
    /* [Build plugin & task ] ------------------------------------*/
    grunt.loadNpmTasks('grunt-module-dependence');
    grunt.loadNpmTasks('grunt-replace');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');

    var pkg = grunt.file.readJSON('package.json');

    var banner = '/*!\n' +
        ' * ====================================================\n' +
        ' * <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
        '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
        '<%= pkg.homepage ? " * " + pkg.homepage + "\\n" : "" %>' +
        ' * GitHub: <%= pkg.repository.url %> \n' +
        ' * Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
        ' Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %>\n' +
        ' * ====================================================\n' +
        ' */\n\n';

    var expose = '\nuse(\'expose-kityminder\');\n';

    // Project configuration.
    grunt.initConfig({

        // Metadata.
        pkg: pkg,

        clean: {
            last: 'dist'
        },

        // resolve dependence
        dependence: {
            options: {
                base: 'src',
                entrance: 'expose-kityminder'
            },
            merge: {
                files: [{
                    src: 'src/**/*.js',
                    dest: 'dist/kityminder.core.js'
                }]
            }
        },

        // concat, just add closure
        concat: {
            options: {
                banner: banner + '(function () {\n',
                footer: expose + '})();'
            },
            build: {
                files: {
                    'dist/kityminder.core.js': ['dist/kityminder.core.js']
                }
            }
        },

        uglify: {
            options: {
                banner: banner
            },
            minimize: {
                src: 'dist/kityminder.core.js',
                dest: 'dist/kityminder.core.min.js'
            }
        },

        copy: {
            dist: {
                src: 'src/kityminder.css',
                dest: "dist/kityminder.core.css"
            }
        }

    });


    // Build task(s).
    grunt.registerTask('build', ['clean', 'dependence', 'concat:build', 'uglify:minimize', 'copy']);

};