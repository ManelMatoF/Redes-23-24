package es.udc.redes.tutorial.info;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class Info {

    public static void main(String[] args) throws NoSuchFileException {
        //PREGUNTAR SI VA BIEN
        Path path = Paths.get("../../../p0-files/" + args[0]).toAbsolutePath().normalize();

        if(!Files.exists(path)){
            throw new NoSuchFileException("La ruta no existe: " + path);
        }

        File file = path.toFile();

        String nombre = file.getName();

        String rutaAbsoluta = file.getAbsolutePath();

        long tamaño = file.length();

        long modificacion = file.lastModified();
        Date fechaModificacion = new Date(modificacion);

        String extension = nombre.substring(nombre.lastIndexOf(".") + 1);

        System.out.println("Nombre: " + nombre);
        System.out.println("Extension: " + extension);
        System.out.println("Tipo: " + getFileType(path));
        System.out.println("Tamano: " + tamaño + " bytes");
        System.out.println("Ultima modifccacion: " + fechaModificacion);
        System.out.println("Absolute Path: " + rutaAbsoluta);
    }
    private static String getFileType(Path path) {
        if (Files.isDirectory(path)) {
            return "Directorio";
        } else if (Files.isRegularFile(path)) {
            String fileName = path.getFileName().toString().toLowerCase();
            if (fileName.endsWith(".txt")) {
                return "Texto";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif")) {
                return "Imagen";
            } else {
                return "Unknown";
            }
        } else {
            return "Unknown";
        }
    }
}
