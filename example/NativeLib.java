package example;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

public final class NativeLib {

    /* Statically load the library which contains all native functions used in here */
    static private boolean isNativeInited = false;
    static String name = "example_swig";

    public static String getPath() {
        String prefix = "NATIVE";
        String os = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");

        if (os.toLowerCase().startsWith("^win"))
            os = "Windows";
        else if (os.contains("OS X"))
            os = "Darwin";

        if (arch.matches("^i[3-6]86$"))
            arch = "x86";
        else if (arch.equalsIgnoreCase("amd64"))
            arch = "x86_64";

        os = os.replace(' ', '_');
        arch = arch.replace(' ', '_');

        return prefix + "/" + os + "/" + arch + "/";
    }
    public static void nativeInit() {
	if (isNativeInited)
            return;
        try {
            /* prefer the version on disk, if existing */
            System.loadLibrary(name);
        } catch (UnsatisfiedLinkError e) {
            /* If not found, unpack the one bundled into the jar file and use it */
            loadLib(name);
        }
        isNativeInited = true;
    }

    private static void loadLib (String name) {
        String Path = NativeLib.getPath();

        String filename=name;
        InputStream in = NativeLib.class.getClassLoader().getResourceAsStream(Path+filename);
		
        if (in == null) {
	    filename = "lib"+name+".so";
            in = NativeLib.class.getClassLoader().getResourceAsStream(Path+filename);
        } 
	if (in == null) {
			filename = name+".dll";
			in = example.class.getClassLoader().getResourceAsStream(Path+filename);
		}  
		if (in == null) {
			filename = "lib"+name+".dll";
			in = example.class.getClassLoader().getResourceAsStream(Path+filename);
		}  
		if (in == null) {
			filename = "lib"+name+".dylib";
			in = example.class.getClassLoader().getResourceAsStream(Path+filename);
		}  
		if (in == null) {
			throw new RuntimeException("Cannot find library "+name+" in path "+Path+". Sorry, but this jar does not seem to be usable on your machine.");
		}
// Caching the file on disk: desactivated because it could fool the users 		
//		if (new File(filename).isFile()) {
//			// file was already unpacked -- use it directly
//			System.load(new File(".").getAbsolutePath()+File.separator+filename);
//			return;
//		}
		try {
			// We must write the lib onto the disk before loading it -- stupid operating systems
			File fileOut = new File(filename);
//			if (!new File(".").canWrite()) {
//				System.out.println("Cannot write in ."+File.separator+filename+"; unpacking the library into a temporary file instead");
				fileOut = File.createTempFile(name+"-", ".tmp");
				// don't leak the file on disk, but remove it on JVM shutdown
				Runtime.getRuntime().addShutdownHook(new Thread(new FileCleaner(fileOut.getAbsolutePath())));
//			}
//			System.out.println("Unpacking SimGrid native library to " + fileOut.getAbsolutePath());
			OutputStream out = new FileOutputStream(fileOut);
			
			/* copy the library in position */  
			byte[] buffer = new byte[4096]; 
			int bytes_read; 
			while ((bytes_read = in.read(buffer)) != -1)     // Read until EOF
				out.write(buffer, 0, bytes_read); 
		      
			/* close all file descriptors, and load that shit */
			in.close();
			out.close();
			System.load(fileOut.getAbsolutePath());
		} catch (Exception e) {
			System.err.println("Cannot load the bindings to the "+name+" library: ");
			e.printStackTrace();
			System.err.println("This jar file does not seem to fit your system, sorry");
			System.exit(1);
		}
	}		
	/* A hackish mechanism used to remove the file containing our library when the JVM shuts down */
	private static class FileCleaner implements Runnable {
		private String target;
		public FileCleaner(String name) {
			target = name;
		}
        public void run() {
            try {
                new File(target).delete();
            } catch(Exception e) {
                System.out.println("Unable to clean temporary file "+target+" during shutdown.");
                e.printStackTrace();
            }
        }    
	}


    public static void main(String[] args) {
        System.out.println(getPath());
    }
}
