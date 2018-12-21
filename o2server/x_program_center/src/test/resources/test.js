var CipherConnectionActionClass = Java.type('com.x.base.core.project.connection.CipherConnectionAction');
var ConfigClass = Java.type('com.x.base.core.project.config.Config');

var resp = CipherConnectionActionClass.post(false, ConfigClass.x_program_centerUrlRoot() + 'invoke/flag/impower/execute', '{"from":"周睿","to":"楼国栋"}');

print(resp);