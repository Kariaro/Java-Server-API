# Java-Server-API
This project is open source copy, edit, modify or whatever you want to do.

This took me about 7-10 days to complete (im still editing the code from time to time) and it's a Server written in Pure Java.

Writing the Usage info takes so long time..

<h2>Usage</h2>
To create a ServerInstance you need to write.
<pre><code>ServerInstance.createInstance(ping, maxconnections);</code></pre>

If you want to add a page you can write.
<pre><code>ServerInstance.getInstance().addPage(file_path, "/" + location_on_page);</code></pre>

Or more advanced create a DeviceListener to get the request and send back data.
<pre><code>ServerInstance.getInstance().addDeviceListener(new DeviceListener() {
    public void clientDisconnect(DeviceEvent event) {
        // The client disconnected or the timeout has been reached
    }
    public void clientConnection(DeviceEvent event) {
        /* Client connected to your server
         *
         * This listener does not get called if the requested
         * page is listed by addPage()
         *
         * If you consume the request the event gets deleted
         * and won't be displayed on other listeners you add.
         */
        event.consume();
    }
    public void clientTimeout(DeviceEvent event) {
        // This is called when a client is not responding for more than 'timeout' millisecounds
    }
}</code></pre>

To create a HTTP header you can use the class Header... example.
<pre><code>Header H = Header.create();
H.setProtocol(HTTP.HTTP_1_1);
H.setStatus(Status.OK);
H.setLastModified(Date.getDate(1, 1, 23, 1, 2002, 59, 0));
H.setSimpleDate(Date.getDate());
H.setContentType(ContentType.TEXT_PLAIN);
H.setConnection(Connection.KEEP_ALIVE);
// If you want to set some other not specified methods use.
H.set("Cache-Control", "private, no-store, no-cache, max-age=0");</code></pre>

Her is a explanation of most methods inside ServerInstance.
<pre><code>// Starts the server.
startServer();

// Closes the server.
closeServer();

// Restarts the server. Actually deletes the old one and creates a new one with startServer().
// This does not affect the pages added by addPage();
restartServer(); 

// Changes the server port (automatically restarts server)
setPort(int port);

// Changes the max allowed connections to the server (automatically restarts server)
setMaxConnections(int max);

// Amount of timeouts before the client gets droped from the server.
setMaxTimeouts(int max);

// How long it takes in milliseconds before the device adds one to the timeout counter.
setConnectionTimeout(int millis);

// Sets if the server should allow not existing files to be added by addPage();
setIgnoreFileNotFound(boolean b);</code></pre>

That was a quick tutorial about how to use this ServerAPI.

<h2>Info</h2>
This server has not support for DatagramPackets yet (or i don't plan on implementing it)
so you need to write that code yourself

Autor: I don't like making my name public.
