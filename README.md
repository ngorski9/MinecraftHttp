# MinecraftHttp

## About

MinecraftHttp is a plugin to turn your Minecraft server into a dynamic web server. You can use this plugin by itself to host a static website. You can also use your own plugins to write custom route handlers. These handlers can run code to interact with the game. The handlers can handle get or post requests, and can respond to requests with static files, arbitrary strings to be used with AJAX, or filled-in templates using the Liquid Template Language.

The liquid language was implemented using [Liqp](https://github.com/bkiers/Liqp), a Java implementation of Shopify Liquid.

The code for the plugin is in the folder minecraft-http. The folder sample-plugin shows an example of how you can write code in your plugin to interact with minecraft-http.

### Dependencies

This plugin relies on [Liqp](https://github.com/bkiers/Liqp), which in-turn relies on [ANTLR](https://www.antlr.org/) and [Jackson](https://github.com/FasterXML/jackson).

Fortunately, Spigot will manually fetch the dependencies from Maven Central when you enable the plugin, so you do not need to download anything to use this plugin, nor do you need to set up a Maven repository if you want to modify this code.

## Usage

### Connecting to the Web Server

The server has the same ip-address as your Minecraft server, but runs on port 8000 rather than port 25565. You can connect to it by typing this ip address into your browser.

As an example, if your server has ip-address 12.34.56.789:25565, then to connect to the website, you would type 12.34.56.789:8000 into your browser.

### Hosting a Static Website

When you run the plugin, it will create a folder called MinecraftHttp-Static in your server's directory. You can insert files into this folder and it will host them statically.

### Interacting With This Plugin Using Other Plugins

You can extend this plugin by creating another plugin. You do not need to reference this plugin's source code when doing so, although you need to set up your plugin in a certain way:

#### File System

Your plugin must have a folder called MinecraftHttp. This folder should contain two subfolders: static and templates. Any files that you put into the static folder will be hosted statically. The templates folder should hold any templates that you will use with liquid.

There should also be a file called routes.txt. Each line of this file should list all custom routes that you plan on running from your plugin. Each route's code is executed in a server command that you create. By default, the command that corresponds with the route will have the same name as the route (e.g., the /home route will work by running the /home command). However, if you want to run a different command with a route, put the name of the new command that you would like to run after the route name separated by a comma with no spaces. Do not include a backslash in the new command. So, for example, if you want the /home route to run the /sample command, put /home,sample in your routes.txt (you can also refer to the example in sample-plugin).

### Writing Custom Handlers

When you define a custom route, the server handles it by calling a server command which corresponds to that route (see: File System). The server will run the command with two paramters. The first parameter is a JSON dictionary of the get parameters, and the second is a JSON dictionary of the post parameters. For help working with JSON in Java, [see here](https://www.geeksforgeeks.org/parse-json-java/).

To tell the server what you would like to respond with, you should send a message back to the command sender. You can either respond with a string, a static file, or a liquid template.

To respond with a string, simply send the string that you want to respond with back to the sender.

To respond with a static file, respond with the message "file:[file name]". Be sure to include extensions on the filename. Do not include the square brackets.

To respond with a liquid template, respond with "template:[template_name],[arguments]". Template name should be the full filename of the template, including the extension. Arguments should be a string representation of a JSON dictionary. Do not include any spaces before or after the comma. Do not include the square brackets.

To see examples of each of these uses, refer to the example in sample-plugin.
