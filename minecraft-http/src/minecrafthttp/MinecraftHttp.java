package minecrafthttp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.bukkit.plugin.java.JavaPlugin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import liqp.Template;

import java.util.Map;

public class MinecraftHttp extends JavaPlugin{
    
	private final String static_folder_name = "MinecraftHttp static";
	private final String other_plugin_static_folder_name = "MinecraftHttp/static";
	private final String other_plugin_routes_file_name = "MinecraftHttp/routes.txt";
	private final String other_plugin_template_folder_name = "MinecraftHttp/templates";
	
	private final int opsfn_length = other_plugin_static_folder_name.length();
	private final int optfn_length = other_plugin_template_folder_name.length();
	
	HttpServer server;
	
	// Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	
    	try {
    		// set up the server and file root location
            server = HttpServer.create(new InetSocketAddress(8000), 0);
    		File pluginFolder = new File(MinecraftHttp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
    		
    		// handle files from other plugins, including static files, templates, and routes
    		File[] plugin_folder_contents = pluginFolder.listFiles();
    		for(int i = 0; i < plugin_folder_contents.length; i++) {
    			File f = plugin_folder_contents[i];
    			
    			// iterate the jar file
    			if( f.getName().substring( f.getName().length() - 4 ).equals(".jar") ) {
    				// a hashmap where the keys are file names and the values are jarentries. Used for custom routes.
    				Map<String, JarEntry> current_plugin_template_map = new HashMap<String, JarEntry>();
    				Map<String, JarEntry> current_plugin_static_file_map = new HashMap<String, JarEntry>();
    			    JarFile jarFile = new JarFile( f.getPath() );
    			    Enumeration<JarEntry> e = jarFile.entries();
    			    while (e.hasMoreElements()) {
    			    	JarEntry jarEntry = (JarEntry)e.nextElement();
    			    	String name = jarEntry.getName();
    			    	
    			    	// if a file is identified as the routes file, open it and add the routes
    			    	if(name.equals(other_plugin_routes_file_name)) {
    			    		// read through the lines of the file
    			    		BufferedReader br = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)));
    			    		while (br.ready()) {
    			    			String s = br.readLine();
    			    			// if there is a comma, make the command different from the route name
    			    			if (s.contains(",")){
    			    				int breakpoint = s.indexOf(",");
    			    				server.createContext(s.substring(0, breakpoint-1), new CustomRouteHandler(s.substring(breakpoint+1), jarFile, current_plugin_template_map, current_plugin_static_file_map, this));
    			    			}
    			    			else {
    			    				// if there is not a comma, make the command the same as the route name
        			    			server.createContext(s, new CustomRouteHandler(s.substring(1), jarFile, current_plugin_template_map, current_plugin_static_file_map, this));
    			    			}
    			    		}
    			    	}
    			    	
    			    	// if a file is identified to be inside the static folder, then set it as a context
    			    	else if(name.length() > opsfn_length && name.substring(0, opsfn_length).equals(other_plugin_static_folder_name)) {
    			    		String fname = name.substring(opsfn_length);
    			    		
    			    		// for index.html, create a route without '/index.html'
    			    		if(fname.length() >= 11 && fname.substring(fname.length()-11).equals("/index.html")) {
        			    		server.createContext(name.substring(opsfn_length, name.length()-10), new JarEntryHandler(jarFile, jarEntry));
    			    		}

    			    		// create context for things that aren't /index.html (this still allows the route /index.html)
        			    	server.createContext(name.substring(opsfn_length), new JarEntryHandler(jarFile, jarEntry));    			    			
    			    	
        			    	// add the jar entry to the static file map
        			    	current_plugin_static_file_map.put(name.substring(opsfn_length+1), jarEntry);
    			    	}
    			    	
    			    	// if a file is identified to be inside the templates folder, then add the template to the template map
    			    	else if(name.length() > optfn_length && name.substring(0, optfn_length).equals(other_plugin_template_folder_name)) {
    			    		current_plugin_template_map.put(name.substring(optfn_length+1), jarEntry);
    			    	}
    			    }
    				
    			}
    		}
    		
    		// handle static files from static folder
    		File staticFolder = new File(pluginFolder, "../" + static_folder_name);
    		if( !staticFolder.exists() ) {
    			// if there is no static folder, then make one
    			getLogger().info( "No static webpage folder exists. Creating it.");
    			staticFolder.mkdir();
    		}
    		else if( staticFolder.isDirectory() ){
    			// if there is a static folder, iterate it
    			iterateStaticFolder(staticFolder, "/");
    		}
    		else {
    			// if there is a file named what the static folder should be, notify this.
    			getLogger().info("A file exists called '" + static_folder_name + "' which is not a directory. Thus, we cannot have a static webpage folder.");
    		}
            
            // start the server
            server.setExecutor(null); // creates a default executor
            server.start();    		
    	}
    	catch(Exception e) {
    		server.stop(0);
    		e.printStackTrace();
    	}
    	
    }
    
    // iterates through the static folder directory and adds files.
    // turns these files into routes
    private void iterateStaticFolder(File root, String url) {
    	File[] files = root.listFiles();
    	for(int i = 0; i < files.length; i++) {
    		File f = files[i];
    		if(f.isDirectory()) {
    			iterateStaticFolder(f, url + f.getName() + "/");
    		}
    		else {
    			if(f.getName().equals("index.html")) {
    				server.createContext(url, new FileHandler(f));
    			}

   				server.createContext(url + f.getName(), new FileHandler(f));
  
    		}
    	}
    }
    
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    	if(server != null) {
    		server.stop(0);
    	}
    }
	
    // this is what responds with files
    static class FileHandler implements HttpHandler {
    	
    	private File file;
    	public FileHandler(File f) {
    		file = f;
    	}
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	
        	t.sendResponseHeaders(200, file.length());

        	OutputStream outputStream=t.getResponseBody();
        	Files.copy(file.toPath(), outputStream);
        	outputStream.close();
        }
    } 
    
    static class JarEntryHandler implements HttpHandler{
    	
    	private JarFile jarFile;
    	private JarEntry jarEntry;
    	public JarEntryHandler(JarFile f, JarEntry e) {
    		jarFile = f;
    		jarEntry = e;
    	}
    	
        @Override
        public void handle(HttpExchange t) throws IOException {
        	
        	InputStream inputStream = jarFile.getInputStream(jarEntry);
        	byte[] input_array = inputStream.readAllBytes();
        	
        	t.sendResponseHeaders(200, input_array.length);

        	OutputStream outputStream=t.getResponseBody();
        	outputStream.write(input_array);
        	outputStream.close();
        }
    	
    }
    
    static class CustomRouteHandler implements HttpHandler{
    	
    	private JarFile jarfile_of_origin;
    	private Map<String, JarEntry> plugin_templates;
    	private Map<String, JarEntry> plugin_static_files;
    	
    	private String command;
    	private SynchronousSender ss;
    	
    	public CustomRouteHandler(String command, JarFile jarfile, Map<String, JarEntry> plugin_templates, Map<String, JarEntry> plugin_static_files, JavaPlugin p) {
    		this.command = command;
    		jarfile_of_origin = jarfile;
    		this.plugin_templates = plugin_templates;
    		this.plugin_static_files = plugin_static_files;
    		ss = new SynchronousSender(p);
    	}
    	
    	public String url_parameters_to_json(String input) {
    		if(input.length() == 0) {
    			return "{}";
    		}
    		
    		// morph the url string into a JSON dictionary
    		input = input.replace("=", "\":\"");
    		input = input.replace("&", "\",\"");
    		input = input.replace("+", " ");
    		
    		// handle characters which are escaped both in JSON and URLs
    		input = input.replace("%5C", "\\\\");
    		input = input.replace("%22", "\\\"");
    		
    		// add curly brackets and unescape the rest of the URL escape characters
    		return "{\"" + URLDecoder.decode(input, StandardCharsets.UTF_8) + "\"}";
    	}
    	
    	public void handle(HttpExchange t) throws IOException {
    		String get_params_string = t.getRequestURI().toString();
    		get_params_string = get_params_string.substring(get_params_string.indexOf('?') + 1);
    		
            String post_params_string = new BufferedReader(
                    new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                      .lines()
                      .collect(Collectors.joining("\n"));
    		
    		String get_params_json = url_parameters_to_json(get_params_string);
    		String post_params_json = url_parameters_to_json(post_params_string);
    		
    		String response = ss.captureCommand(command + " " + get_params_json + " " + post_params_json);
    		
    		String output;
    		
    		if(response.substring(0,5).equals("file:")) {
    			/*
    			 *  if the plugin says to host a static file, do so. Since this doesn't align with the other
    			 *  types of responses which return strings, this code block sets up the outputstream
    			 *  manually and returns
    			 */
    			
    			InputStream inputStream = jarfile_of_origin.getInputStream(plugin_static_files.get(response.substring(5)));
            	byte[] input_array = inputStream.readAllBytes();
            	
            	t.sendResponseHeaders(200, input_array.length);

            	OutputStream outputStream=t.getResponseBody();
            	outputStream.write(input_array);
            	outputStream.close();
            	// this is so we don't send something twice!
            	return;
    		}
    		if(response.substring(0,9).equals("template:")) {
    			int comma_point = response.indexOf(",");
    			String template_name = response.substring(9, comma_point);
    			String json = response.substring(comma_point+1);
    			
    			try {
	    			String templateString = new String(jarfile_of_origin.getInputStream(plugin_templates.get(template_name)).readAllBytes());
	    			Template template = Template.parse(templateString);
	    			output = template.render(json);
    			}
    			catch(Exception e) {
    				output = "this failed";
    				e.printStackTrace();
    			}
    		}
    		else {
    			output = response;
    		}
    		
    		t.sendResponseHeaders(200, output.length());
    		OutputStream outputStream = t.getResponseBody();
    		Writer o = new BufferedWriter(new OutputStreamWriter(outputStream));
    		o.append(output);
    		o.flush();
    		o.close();
    		outputStream.close();
    	}
    }
}
