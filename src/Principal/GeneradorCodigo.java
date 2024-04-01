package Principal;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author kyron
 */
public class GeneradorCodigo {
    public static Stack<String> pilaSegmentos = new Stack<>();
    public static Map<String, String> tablaSimbolos = new HashMap<>();
    public static int enteros = 0, flotantes = 0, boleanos = 0, cadenas = 0,
            imprimir = 0, leer = 0;
    public static String codigo = "", variables = "cad db 13,10,'$'\n";
    public static void generadorCodigo(){
         String DECLARACION = AnSintactico.TIPOVARIABLES,
                 VARIABLES = AnSintactico.TABLASIMBOLOS;
         codigo += ".MODEL SMALL\n" +
                 ".CODE\n" +
                 "Inicio:\n" +
                 "mov ax, @Data\n" +
                 "mov ds, ax\n";
         for (String linea : Main.codigoLineas) {
             AnLexico.lineaTokens.clear();
             AnLexico.tokenizador(linea);
             String auxiliar = AnLexico.lineaTokens.get(0);
             if (auxiliar.matches(DECLARACION)){
                codigo += declaracion(auxiliar, AnLexico.lineaTokens.get(2));
             } else if (auxiliar.matches(VARIABLES)) {
                 if (auxiliar.matches(AnSintactico.TABSIMENTEROS)) {
                     if (AnLexico.lineaTokens.size() == 3){
                         codigo += asignacion("entero", auxiliar, AnLexico.lineaTokens.get(2));
                     } else {
                         codigo += operacionAritmetica("entero", auxiliar, AnLexico.lineaTokens.get(2),
                                 AnLexico.lineaTokens.get(3), AnLexico.lineaTokens.get(4));
                     }
                 } else if (auxiliar.matches(AnSintactico.TABSIMFLOTANTES)){
                     if (AnLexico.lineaTokens.size() == 5 && AnLexico.lineaTokens.get(2).matches("^\\d+$")){
                         codigo += asignacion("flotante", auxiliar, AnLexico.lineaTokens.get(2)+"."+AnLexico.lineaTokens.get(4));
                     } else {
                         String valor1 = "", operador = "", valor2 = "";
                         int valor2inicio = 0;
                         if (AnLexico.lineaTokens.get(2).matches("^\\d+$")){
                             valor1 = AnLexico.lineaTokens.get(2)+"."+AnLexico.lineaTokens.get(4);
                             operador = AnLexico.lineaTokens.get(5);
                             valor2inicio = 6;
                         } else {
                             valor1 = AnLexico.lineaTokens.get(2);
                             operador = AnLexico.lineaTokens.get(3);
                             valor2inicio = 4;
                         }
                         if (AnLexico.lineaTokens.get(valor2inicio).matches("^\\d+$")) valor2 = AnLexico.lineaTokens.get(valor2inicio)
                                 +"."+AnLexico.lineaTokens.get(valor2inicio+2);
                         else valor2 = AnLexico.lineaTokens.get(valor2inicio);
                         codigo += operacionAritmetica("flotante", auxiliar, valor1, operador, valor2);
                     }
                 } else if (auxiliar.matches(AnSintactico.TABSIMBOLEANOS)) {
                     codigo += asignacion("boleano", auxiliar, AnLexico.lineaTokens.get(2));
                 } else {
                     if (AnLexico.lineaTokens.size() == 3){
                         codigo += asignacion("cadena", auxiliar, AnLexico.lineaTokens.get(2));
                     } else {
                         codigo += operacionAritmetica("cadena", auxiliar, AnLexico.lineaTokens.get(2),
                                 "+", AnLexico.lineaTokens.get(4));
                     }
                 }
             } else if (auxiliar.matches("}")) {
                if (AnLexico.lineaTokens.size() == 1){
                    codigo += finSegmento();
                } else {
                    codigo += delocontrario();
                }
             } else {
                 String parametros = linea.substring(linea.indexOf('(') + 1, linea.indexOf(')'));
                 switch (auxiliar){
                     case "IMPRIMIR":
                         codigo += imprimir(AnLexico.lineaTokens.get(2));
                         break;
                     case "LEER":
                         codigo += leer(AnLexico.lineaTokens.get(2));
                         break;
                     case "SI":
                         codigo += si(parametros);
                         break;
                     case "PARA":
                         codigo += para(AnLexico.lineaTokens.get(2), AnLexico.lineaTokens.get(5));
                         break;
                     case "MIENTRAS":
                         codigo += mientras(parametros);
                         break;
                 }
             }
         }
         codigo += "mov ax, 4c00h\n" +
                 "int 21h\n" +
                 ".DATA\n" +
                 variables +
                 ".STACK\n" +
                 "END Inicio";
         escribirDocumento(codigo);
    }
    public static String declaracion(String tipo, String id){
        System.out.println(tipo+" "+id);
        switch (tipo){
            case "ENTERO":
                    tablaSimbolos.put(id, "ent"+enteros);
                    variables += "ent"+enteros+" db 100 dup('$')\n";
                    enteros++;
                break;
        }
        return "";
    }
    public static String asignacion(String tipo, String id, String valor){
        System.out.println(tipo+" "+id+" "+valor);
        return "";
    }
    public static String operacionAritmetica(String tipo, String id, String valor1, String operador, String valor2){
        System.out.println(tipo+" "+id+" "+valor1+" "+operador+" "+valor2);
        return "";
    }
    public static String imprimir(String tipo){
        System.out.println(tipo);
        if (tipo.matches("'\\d'")){
            int cadena = Integer.parseInt(tipo.substring(1,tipo.length()-1));
            tipo = Main.cadenasCodigo.get(cadena);
            variables += "imp"+imprimir+" db 13,10,'"+tipo+"$'\n";
            tipo = "imp"+imprimir;
            imprimir++;
            return "lea dx, "+tipo+"\n" +
                    "mov ah, 9\n" +
                    "int 21h\n";
        }else{
            tipo = tablaSimbolos.get(tipo);
            return "mov ah, 9\n" +
                    "lea dx, "+tipo+"\n" +
                    "int 21h\n" +
                    "lea dx, cad\n" +
                    "int 21h\n";
        }
    }
    public static String leer(String id){
        System.out.println(id);
        id = tablaSimbolos.get(id);
        id = "leer"+leer+":\n" +
                "mov ax, 0\n" +
                "mov ah, 1\n" +
                "int 21h\n" +
                "mov "+id+"[si], al\n" +
                "inc si\n" +
                "cmp al, 0dh\n" +
                "ja leer"+leer+"\n" +
                "jb leer"+leer+"\n" +
                "mov ah, 9\n" +
                "lea dx, cad\n" +
                "int 21h\n";
        leer++;
        return id;
    }
    public static String si(String parametros){
        System.out.println(parametros);
        return "";
    }
    public static String para(String inicio, String fin){
        System.out.println(inicio+" "+fin);
        return "";
    }
    public static String mientras(String parametros){
        System.out.println(parametros);
        return "";
    }
    public static String delocontrario(){
        System.out.println("eldelocontrario");
        return "";
    }
    public static String finSegmento(){
        System.out.println("findelsegmento");
        return "";
    }
    public static void escribirDocumento(String codigo){
        try {
            PrintWriter writer = new PrintWriter(Main.nombreArchivo+".asm");
            writer.print(codigo);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
