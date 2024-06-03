package Principal;

import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kyron
 */
public class GeneradorCodigo {
    public static Stack<String> pilaSegmentos = new Stack<>();
    public static Map<String, String> tablaSimbolos = new HashMap<>();
    public static int enteros = 0, flotantes = 0, boleanos = 0, cadenas = 0,
            imprimir = 0, potencia = -1, comparacion = -1, para = -1;
    public static String codigo = "", variables = "cad db 13,10,'$'\n\n; Variables del programa\n";
    public static void generadorCodigo(){
         String DECLARACION = AnSintactico.TIPOVARIABLES,
                 VARIABLES = AnSintactico.TABLASIMBOLOS;
         codigo += ".MODEL SMALL\n" +
                 ".CODE\n" +
                 "Main:\n" +
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
         codigo += "\n; FIN DEL PROGRAMA\n" +
                 "mov ax, 4c00h\n" +
                 "int 21h\n" +
                 ".DATA\n" +
                 "AUX db 3, 0, 3 dup (?)\n" +
                 "S db 1 dup (10,13), \"$\"\n" +
                 variables +
                 "\n.STACK\n" +
                 "END Main";
         escribirDocumento(codigo);
    }
    public static String declaracion(String tipo, String id){
        System.out.println(tipo+" "+id);
        switch (tipo){
            case "ENTERO":
                    tablaSimbolos.put(id, "ent"+enteros);
                    variables += "ent"+enteros+" dw 0, 10, 13, \"$\"\n";
                    enteros++;
                break;
            case "CADENA":
                tablaSimbolos.put(id, "cad"+cadenas);
                variables += "cad"+cadenas+" db 40,?,40 dup (\"$\")\n";
                enteros++;
                break;
        }
        return "";
    }
    public static String asignacion(String tipo, String id, String valor){
        System.out.println(tipo+" "+id+" "+valor);
        switch (tipo){
            case "entero":
                id = tablaSimbolos.get(id);
                if (!valor.matches("\\d+")) valor = tablaSimbolos.get(valor);
                return "\n; Asignacion de valor\n" +
                        "mov ax, "+valor+"\n" +
                        "mov "+id+", ax\n";
        }
        return "";
    }
    public static String operacionAritmetica(String tipo, String id, String valor1, String operador, String valor2){
        System.out.println(tipo+" "+id+" "+valor1+" "+operador+" "+valor2);
        switch (tipo) {
            case "entero":
                id = tablaSimbolos.get(id);
                if (!valor1.matches("\\d+")) valor1 = tablaSimbolos.get(valor1);
                if (!valor2.matches("\\d+")) valor2 = tablaSimbolos.get(valor2);
                switch (operador){
                    case "+":
                        return "\n; Suma\n" +
                                "mov ax, "+valor1+"\n" +
                                "mov bx, "+valor2+"\n" +
                                "add ax, bx\n" +
                                "mov "+id+", ax\n";
                    case "-":
                        return "\n; Resta\n" +
                                "mov ax, "+valor1+"\n" +
                                "mov bx, "+valor2+"\n" +
                                "sub ax, bx\n" +
                                "mov "+id+", ax\n";
                    case "*":
                        return "\n; Producto\n" +
                                "mov ax, "+valor1+"\n" +
                                "mov bx, "+valor2+"\n" +
                                "mul bl\n" +
                                "mov "+id+", ax\n";
                    case "/":
                        return "\n; Division\n" +
                                "mov ax, "+valor1+"\n" +
                                "mov bx, "+valor2+"\n" +
                                "div bl\n" +
                                "mov "+id+", ax\n";
                    case "%":
                        return "\n; Modulo\n" +
                                "mov ax, "+valor1+"\n" +
                                "mov bx, "+valor2+"\n" +
                                "div bl\n" +
                                "mov al, ah\n" +
                                "cbw\n" +
                                "mov "+id+", ax\n";
                    case "^":
                        potencia++;
                        return "\n; Potenciacion\n" +
                                "mov ax, "+valor1+"\n" +
                                "mov bx, "+valor1+"\n" +
                                "mov cx, "+valor2+"\n" +
                                "potencialoop"+potencia+":\n" +
                                "cmp cx, 1\n" +
                                "je donepot"+potencia+"\n" +
                                "mul bx\n" +
                                "dec cx\n" +
                                "jmp potencialoop"+potencia+"\n" +
                                "donepot"+potencia+":\n" +
                                "mov "+id+", ax\n";
                }
        }
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
            return "\n; Imprimir cadena\n" +
                    "lea dx, "+tipo+"\n" +
                    "mov ah, 9\n" +
                    "int 21h\n";
        }else{
            tipo = tablaSimbolos.get(tipo);
            switch (tipo.substring(0,3)) {
                case "ent":
                    return "\n; Imprimir entero\n" +
                            "mov Ah, 09h\n" +
                            "mov Dx, offset S\n" +
                            "int 21h\n" +
                            "add "+tipo+", 30h\n" +
                            "mov Ah, 09h\n" +
                            "mov Dx, offset "+tipo+"\n" +
                            "int 21h\n" +
                            "sub "+tipo+", 30h\n";
                case "cad":
                    return "\n; Imprimir variable cadena\n" +
                            "mov Ah, 09h\n" +
                            "mov Dx, offset S\n" +
                            "int 21h\n" +
                            "mov Ah, 09h\n" +
                            "mov Dx, offset "+tipo+"+2\n" +
                            "int 21h\n";
            }
        }
        return "";
    }
    public static String leer(String id){
        System.out.println(id);
        id = tablaSimbolos.get(id);
        switch (id.substring(0,3)) {
            case "ent":
                return "\n; Lectura de entero\n" +
                        "mov ah, 0Ah\n" +
                        "mov Dx, offset AUX\n" +
                        "int 21h\n" +
                        "mov Si, offset AUX+2\n" +
                        "mov Ah, 0\n" +
                        "mov al, byte ptr [Si]\n" +
                        "sub ax, 30h\n" +
                        "mov "+id+", ax\n" +
                        "mov Ah, 09h\n" +
                        "mov Dx, offset S\n" +
                        "int 21h\n";
            case "cad":
                return "\n; Lectura de cadena\n" +
                        "mov Ah, 0Ah\n" +
                        "mov Dx, offset "+id+"\n" +
                        "int 21h\n";
        }
        return id;
    }
    public static String si(String parametros){
        System.out.println(parametros);
        // COMPUESTO
        if (parametros.contains("&") || parametros.contains("|")) {
            Pattern pattern = Pattern.compile("(.+?)([&|])(.+)");
            Matcher matcher = pattern.matcher(parametros);
            String parte1 = "", operador = "", parte2 = "";
            if (matcher.find()) {
                parte1 = matcher.group(1).trim();
                operador = matcher.group(2).trim();
                parte2 = matcher.group(3).trim();
            } else {
                System.out.println("No se encontró un operador válido en la cadena.");
            }
            List<String> tokensParte1 = AnLexico.tokenizadorComparacion(parte1);
            List<String> tokensParte2 = AnLexico.tokenizadorComparacion(parte2);
            String comparacion1 = "", comparacion2 = "", fincomp = "";
            if (!tokensParte1.getFirst().matches("\\d+")) tokensParte1.set(0, tablaSimbolos.get(tokensParte1.getFirst()));
            if (!tokensParte1.get(2).matches("\\d+")) tokensParte1.set(2, tablaSimbolos.get(tokensParte1.get(2)));
            comparacion++;
            fincomp += "fincomp"+comparacion+":\n";
            switch (tokensParte1.get(1)) {
                case "==":
                    comparacion1 += "\n; Comparacion igual igual 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "je comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "!=":
                    comparacion1 += "\n; Comparacion desigual 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jne comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "<=":
                    comparacion1 += "\n; Comparacion menor igual 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jle comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case ">=":
                    comparacion1 += "\n; Comparacion mayor igual 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jge comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "<":
                    comparacion1 += "\n; Comparacion menor 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jl comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case ">":
                    comparacion1 += "\n; Comparacion mayor 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jg comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
            }
            if (!tokensParte2.getFirst().matches("\\d+")) tokensParte2.set(0, tablaSimbolos.get(tokensParte2.getFirst()));
            if (!tokensParte2.get(2).matches("\\d+")) tokensParte2.set(2, tablaSimbolos.get(tokensParte2.get(2)));
            comparacion++;
            fincomp += "fincomp"+comparacion+":\n";
            switch (tokensParte2.get(1)) {
                case "==":
                    comparacion2 += "\n; Comparacion igual igual 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "je comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "!=":
                    comparacion2 += "\n; Comparacion desigual 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jne comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "<=":
                    comparacion2 += "\n; Comparacion menor igual 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jle comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case ">=":
                    comparacion2 += "\n; Comparacion mayor igual 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jge comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "<":
                    comparacion2 += "\n; Comparacion menor 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jl comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case ">":
                    comparacion2 += "\n; Comparacion mayor 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jg comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
            }
            pilaSegmentos.add(fincomp);
            if (operador.matches("&")) return comparacion1+comparacion2;
        // SIMPLE
        } else {
            List<String> tokens = AnLexico.tokenizadorComparacion(parametros);
            System.out.println(tokens);
            if (!tokens.get(0).matches("\\d+")) tokens.set(0, tablaSimbolos.get(tokens.get(0)));
            if (!tokens.get(2).matches("\\d+")) tokens.set(2, tablaSimbolos.get(tokens.get(2)));
            comparacion++;
            switch (tokens.get(1)) {
                case "==":
                    pilaSegmentos.add("fincomp"+comparacion+":\n");
                    return "\n; Comparacion igual igual\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "je comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                case "!=":
                    pilaSegmentos.add("fincomp"+comparacion+":\n");
                    return "\n; Comparacion desigual\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jne comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                case "<=":
                    pilaSegmentos.add("fincomp"+comparacion+":\n");
                    return "\n; Comparacion menor igual\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jle comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                case ">=":
                    pilaSegmentos.add("fincomp"+comparacion+":\n");
                    return "\n; Comparacion mayor igual\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jge comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                case "<":
                    pilaSegmentos.add("fincomp"+comparacion+":\n");
                    return "\n; Comparacion menor\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jl comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                case ">":
                    pilaSegmentos.add("fincomp"+comparacion+":\n");
                    return "\n; Comparacion mayor\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jg comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
            }
        }
        return "";
    }
    public static String para(String inicio, String fin){
        System.out.println(inicio+" "+fin);
        inicio = tablaSimbolos.get(inicio);
        if (!fin.matches("\\d+")) fin = tablaSimbolos.get(fin);
        para++;
        pilaSegmentos.add("inc "+inicio+"\njmp para"+para+"\nparafin"+para+":\n");
        return "\n; Ciclo for\n" +
                "para"+para+":\n" +
                "mov ax, "+inicio+"\n" +
                "mov bx, "+fin+"\n" +
                "cmp ax, bx \n" +
                "jge parafin"+para+"\n";
    }
    public static String mientras(String parametros){
        System.out.println(parametros);
        System.out.println(parametros);
        // COMPUESTO
        if (parametros.contains("&") || parametros.contains("|")) {
            Pattern pattern = Pattern.compile("(.+?)([&|])(.+)");
            Matcher matcher = pattern.matcher(parametros);
            String parte1 = "", operador = "", parte2 = "";
            if (matcher.find()) {
                parte1 = matcher.group(1).trim();
                operador = matcher.group(2).trim();
                parte2 = matcher.group(3).trim();
            } else {
                System.out.println("No se encontró un operador válido en la cadena.");
            }
            List<String> tokensParte1 = AnLexico.tokenizadorComparacion(parte1);
            List<String> tokensParte2 = AnLexico.tokenizadorComparacion(parte2);
            String comparacion1 = "", comparacion2 = "", fincomp = "";
            if (!tokensParte1.getFirst().matches("\\d+")) tokensParte1.set(0, tablaSimbolos.get(tokensParte1.getFirst()));
            if (!tokensParte1.get(2).matches("\\d+")) tokensParte1.set(2, tablaSimbolos.get(tokensParte1.get(2)));
            comparacion++;
            fincomp += "fincomp"+comparacion+":\n";
            switch (tokensParte1.get(1)) {
                case "==":
                    comparacion1 += "\n; Comparacion igual igual 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "je comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "!=":
                    comparacion1 += "\n; Comparacion desigual 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jne comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "<=":
                    comparacion1 += "\n; Comparacion menor igual 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jle comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case ">=":
                    comparacion1 += "\n; Comparacion mayor igual 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jge comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "<":
                    comparacion1 += "\n; Comparacion menor 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jl comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case ">":
                    comparacion1 += "\n; Comparacion mayor 1\n" +
                            "mov ax, "+tokensParte1.get(0)+"\n" +
                            "mov bx, "+tokensParte1.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jg comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
            }
            if (!tokensParte2.getFirst().matches("\\d+")) tokensParte2.set(0, tablaSimbolos.get(tokensParte2.getFirst()));
            if (!tokensParte2.get(2).matches("\\d+")) tokensParte2.set(2, tablaSimbolos.get(tokensParte2.get(2)));
            comparacion++;
            fincomp += "fincomp"+comparacion+":\n";
            switch (tokensParte2.get(1)) {
                case "==":
                    comparacion2 += "\n; Comparacion igual igual 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "je comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "!=":
                    comparacion2 += "\n; Comparacion desigual 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jne comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "<=":
                    comparacion2 += "\n; Comparacion menor igual 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jle comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case ">=":
                    comparacion2 += "\n; Comparacion mayor igual 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jge comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case "<":
                    comparacion2 += "\n; Comparacion menor 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jl comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
                case ">":
                    comparacion2 += "\n; Comparacion mayor 2\n" +
                            "mov ax, "+tokensParte2.get(0)+"\n" +
                            "mov bx, "+tokensParte2.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jg comp"+comparacion+"\n" +
                            "jmp fincomp"+comparacion+"\n" +
                            "comp"+comparacion+":\n";
                    break;
            }
            pilaSegmentos.add(fincomp);
            if (operador.matches("\\+")) return comparacion1+comparacion2;
            // SIMPLE
        } else {
            List<String> tokens = AnLexico.tokenizadorComparacion(parametros);
            System.out.println(tokens);
            if (!tokens.get(0).matches("\\d+")) tokens.set(0, tablaSimbolos.get(tokens.get(0)));
            if (!tokens.get(2).matches("\\d+")) tokens.set(2, tablaSimbolos.get(tokens.get(2)));
            comparacion++;
            pilaSegmentos.add("jmp while"+comparacion+"\n" +
                    "finwhile"+comparacion+":\n");
            switch (tokens.get(1)) {
                case "==":
                    return "\n; Mientras igual igual\n" +
                            "while"+comparacion+":\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "je mientras"+comparacion+"\n" +
                            "jmp finwhile"+comparacion+"\n" +
                            "mientras"+comparacion+":\n";
                case "!=":
                    return "\n; Mientras desigual\n" +
                            "while"+comparacion+":\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jne mientras"+comparacion+"\n" +
                            "jmp finwhile"+comparacion+"\n" +
                            "mientras"+comparacion+":\n";
                case "<=":
                    return "\n; Mientras menor igual\n" +
                            "while"+comparacion+":\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jle mientras"+comparacion+"\n" +
                            "jmp finwhile"+comparacion+"\n" +
                            "mientras"+comparacion+":\n";
                case ">=":
                    return "\n; Mientras mayor igual\n" +
                            "while"+comparacion+":\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jge mientras"+comparacion+"\n" +
                            "jmp finwhile"+comparacion+"\n" +
                            "mientras"+comparacion+":\n";
                case "<":
                    return "\n; Mientras menor\n" +
                            "while"+comparacion+":\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jl mientras"+comparacion+"\n" +
                            "jmp finwhile"+comparacion+"\n" +
                            "mientras"+comparacion+":\n";
                case ">":
                    return "\n; Mientras mayor\n" +
                            "while"+comparacion+":\n" +
                            "mov ax, "+tokens.get(0)+"\n" +
                            "mov bx, "+tokens.get(2)+"\n" +
                            "cmp ax, bx\n" +
                            "jg mientras"+comparacion+"\n" +
                            "jmp finwhile"+comparacion+"\n" +
                            "mientras"+comparacion+":\n";
            }
        }
        return "";
    }
    public static String delocontrario(){
        System.out.println("eldelocontrario");
        return "";
    }
    public static String finSegmento(){
        System.out.println("findelsegmento");
        return "\n; Fin de segmento\n"+pilaSegmentos.pop();
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
