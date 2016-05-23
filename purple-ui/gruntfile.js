module.exports = function(grunt) {

	var modRewrite = require('connect-modrewrite');

	grunt
			.initConfig({
				config : {
					dev : {
						options : {
							variables : {
								'buildPath' : 'builds/dev',
								'jarrPath' : '../purple-server/target/purple-server-1.0.0-SNAPSHOT.jar'
							}
						}
					},
					prod : {
						options : {
							variables : {
								'buildPath' : 'builds/prod'
							}
						}
					}
				},

				ngconstant: {
						  options: {
						    name: 'server-url',
						    wrap: '"use strict";\n\n{%= __ngModule %}'
						  },
						  dev: {
						  	options: {
     							 dest: 'source/assets/js/lib/server-url.js'
    						},
						    constants: {
						      BASE_URL: 'http://localhost:9999/'
						    }
						  },
						  prod: {
						  	options: {
     							 dest: 'source/assets/js/lib/server-url.js'
    						},
						    constants: {
						      BASE_URL: '/'
						    }
						  }
						},
				shell : {
					options : {
						stderr : false,
						stdin : false,
						stdout : false
					},
					startApiServer : {
						command : 'Start java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8001,suspend=n \ -jar <%= grunt.config.get("jarrPath") %>'
					}
				},
				concat : {
					js : {
						src : [ 'source/assets/js/*.js' ],
						dest : '<%= grunt.config.get("buildPath") %>/assets/js/scripts.js'
					},
					jsLib : {
						src : [ 'source/assets/js/lib/highlight.js',
								'source/assets/js/lib/bootstrap-switch.js',
								'source/assets/js/lib/main.js',
								'source/assets/js/lib/calendar.js',
								'source/assets/js/lib/velocity.min.js',
								'source/assets/js/lib/s-next.js',
								'source/assets/js/lib/index.js',
								'source/assets/js/lib/server-url.js',
								'source/assets/js/lib/theia-sticky-sidebar.js',
								'source/assets/js/lib/test.js'
								
						],
						dest : '<%= grunt.config.get("buildPath") %>/assets/js/_lib.js'
					},
					css : {
						src : [ 'source/assets/css/**/*.css' ],
						dest : '<%= grunt.config.get("buildPath") %>/assets/css/styles.css'
					},
					appControllers : {
						src : [ 'source/app/controllers/*.js' ],
						dest : '<%= grunt.config.get("buildPath") %>/app/controllers/controllers.js'
					},
					appServices : {
						src : [ 'source/app/services/*.js' ],
						dest : '<%= grunt.config.get("buildPath") %>/app/services/services.js'
					},
					appFilters : {
						src : [ 'source/app/filters/*.js' ],
						dest : '<%= grunt.config.get("buildPath") %>/app/filters/filters.js'
					}
				},
				bower_concat : {
					all : {
						dest : '<%= grunt.config.get("buildPath") %>/assets/js/_bower.js',
						cssDest : '<%= grunt.config.get("buildPath") %>/assets/css/_bower.css',
						mainFiles : {
							'bootstrap' : [ 'dist/js/bootstrap.js',
									'dist/css/bootstrap.css' ],
							'crypto-js' : [ 'crypto-js.js' ]
						},
						dependencies : {
							'angular' : 'jquery',
							'angular-bootstrap' : 'angular',
							'bootstrap' : 'angular-bootstrap'
						},
						bowerOptions : {
							relative : false
						}
					}
				},
				clean : {
					build : {
						src : [ '<%= grunt.config.get("buildPath") %>/*.html',
								'<%= grunt.config.get("buildPath") %>/app/*',
								'<%= grunt.config.get("buildPath") %>/assets/*' ]
					}
				},
				copy : {
					appHtml : {
						expand : true,
						cwd : 'source/',
						src : [ '*.html' ],
						dest : '<%= grunt.config.get("buildPath") %>/'
					},
					appImages : {
						expand : true,
						cwd : 'source/assets/',
						src : [ 'img/**' ],
						dest : '<%= grunt.config.get("buildPath") %>/assets/'
					},
					angularApp : {
						expand : true,
						cwd : 'source/',
						src : [ 'app/**', '!app/controllers/**',
								'!app/services/**', '!app/partials/**' ],
						dest : '<%= grunt.config.get("buildPath") %>/'
					},
					bootstrapFonts : {
						expand : true,
						cwd : 'bower_components/bootstrap/',
						src : [ 'fonts/*.*' ],
						dest : '<%= grunt.config.get("buildPath") %>/assets/'
					}
				},
				ngAnnotate : {
					options : {
						singleQuotes : true
					},
					app : {
						files : {
							'<%= grunt.config.get("buildPath") %>/app/controllers/controllers.js' : [ '<%= grunt.config.get("buildPath") %>/app/controllers/controllers.js' ],
							'<%= grunt.config.get("buildPath") %>/app/services/services.js' : [ '<%= grunt.config.get("buildPath") %>/app/services/services.js' ],
							'<%= grunt.config.get("buildPath") %>/app/directives/directives.js' : [ '<%= grunt.config.get("buildPath") %>/app/directives/directives.js' ],
							'<%= grunt.config.get("buildPath") %>/app/filters/filters.js' : [ '<%= grunt.config.get("buildPath") %>/app/filters/filters.js' ],
							'<%= grunt.config.get("buildPath") %>/app/partials/partials.js' : [ '<%= grunt.config.get("buildPath") %>/app/partials/partials.js' ]
						}
					}
				},
				uglify : {
					my_target : {
						files : [ {
							expand : true,
							cwd : '<%= grunt.config.get("buildPath") %>',
							src : [ '**/*.js', '!**/*.min.js' ],
							dest : '<%= grunt.config.get("buildPath") %>'
						} ]
					}
				},
				cssmin : {
					target : {
						files : [ {
							expand : true,
							cwd : '<%= grunt.config.get("buildPath") %>/assets/css',
							src : [ '*.css', '!*.min.css' ],
							dest : '<%= grunt.config.get("buildPath") %>/assets/css'
						} ]
					}
				},
				connect : {
					options : {
						port : 81,
						livereload : 35729,
						// change this to '0.0.0.0' to access the server from
						// outside
						hostname : 'localhost'
					},
					livereload : {
						options : {
							open : true,
							base : [ '.tmp',
									'<%= grunt.config.get("buildPath") %>' ],
							// MODIFIED: Add this middleware configuration
							middleware : function(connect, options) {
								var middlewares = [];

								middlewares
										.push(modRewrite([ '^[^\\.]*$ /index.html [L]' ])); // Matches
																							// everything
																							// that
																							// does
																							// not
																							// contain
																							// a
																							// '.'
																							// (period)
								options.base.forEach(function(base) {
									middlewares.push(connect.static(base));
								});
								return middlewares;
							}
						}
					}
				},
				watch : {
					options : {
						spawn : false,
						livereload : true
					},
					scripts : {
						files : [ 'source/**' ],
						tasks : [ 'config:dev', 'clean', 'concat',
								'bower_concat', 'copy', 'html2js' ]
					}
				},
				// grunt-open will open your browser at the project's URL
				open : {
					server : {
						// Gets the port from the connect configuration
						// path: 'http://localhost:<%=
						// connect.server.options.port%>'
						path : 'http://localhost:9999/'
					},
					local : {
						// Gets the port from the connect configuration
						path : 'http://localhost:<%= connect.options.port%>'
					}
				},

				html2js : {
					options : {
						base : 'source',
					// htmlmin: {
					// removeComments: true,
					// collapseWhitespace: true
					// }
					},
					main : {
						src : [ 'source/app/**/*.html' ],
						dest : '<%= grunt.config.get("buildPath") %>/app/partials/partials.js'
					}
				},
				cacheBustPlus: {
					options: {
						deleteOriginals: true,
						rename: true
					},
					files: {
						src: ['<%= grunt.config.get("buildPath") %>/index.html']
					}
		        }
			});

	// Load all npm tasks
	require('load-grunt-tasks')(grunt);

	// Dev build
	grunt.registerTask('default', [ 'config:dev',
			'clean', 'ngconstant:dev','concat', 'bower_concat', 'copy', 'html2js', 'connect',
			'watch' ]);

	// Production Build
	grunt.registerTask('prod', [ 'config:prod', 'clean', 'ngconstant:prod','concat',
			'bower_concat', 'copy', 'html2js', 'ngAnnotate:app', 'uglify',
		 'cacheBustPlus' ]);
};