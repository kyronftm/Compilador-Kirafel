package Principal;

/**
 *
 * @author kyron
 */
public class AnSintactico {
    public static String TIPOVARIABLES = "";
    public static String OPLOGNUM = "";
    public static String OPLOGBOLCAD = "";
    public static String OPARITMETICOS = "";
    public static String MENTERO = "";
    public static String MFLOTANTE = "";
    public static String MBOLEANO = "";
    public static String MCADENA = "";
    public static String TABLASIMBOLOS = "";
    public static String TABSIMENTEROS = "";
    public static String TABSIMFLOTANTES = "";
    public static String TABSIMBOLEANOS = "";
    public static String TABSIMCADENAS = "";
    public static String TIPOENTERO = "";
    public static String TIPOFLOTANTE = "";
    public static String TIPOCADENA = "";
    public static String TIPOBOLEANO = "";
    public static String TIPOGENERAL = "";
    public static String LEER = "";
    public static String IMPRIMIR = "";
    public static String COMPNUM = "";
    public static String COMPBOLCAD = "";
    public static String COMPARACION = "";
    public static String COMPARACIONG = "";
    public static String COMPARACIONCOMPUESTA = "";
    public static String COMPARACIONCOMPUESTAG = "";
    public static String SI = "";
    public static String SIG = "";
    public static String DELOCONTRARIO = "";
    public static String PARA = "";
    public static String PARAG = "";
    public static String MIENTRAS = "";
    public static String MIENTRASG = "";
    public static String ASIGNACIONENTERO = "";
    public static String ASIGNACIONFLOTANTE = "";
    public static String ASIGNACIONBOLEANO = "";
    public static String ASIGNACIONCADENA = "";
    public static String ASIGNACION = "";
    public static String ASIGNACIONG = "";
    public static String OPARITENTERO = "";
    public static String OPARITFLOTANTE = "";
    public static String OPARITMETICAG = "";
    public static String DECLARACIONVARIABLE = "";
    public static String OPARITCADENA = "";
    public static String FINSEGMENTO = "\\}";
    public static String numLinea = "";
    
    public static boolean AnalizadorSintactico(){
        TIPOVARIABLES = "("+AnLexico.VARIABLES+")";
        OPLOGNUM = "(==|!=|<=|>=|<|>)";
        OPLOGBOLCAD = "(==|!=)";
        OPARITMETICOS = "(\\+|-|\\*|/|%|\\^)";
        MENTERO = "("+AnLexico.NUMERO+")";
        MFLOTANTE = "("+MENTERO+"\\."+MENTERO+")";
        MBOLEANO = "(VERDADERO|FALSO)";
        MCADENA = "("+AnLexico.CADENAS+")";
        TABLASIMBOLOS = "("+String.join("|", Main.tablaSimbolos)+")";
        TABSIMENTEROS = "("+String.join("|", Main.tabSimENTEROS)+")";
        TABSIMFLOTANTES = "("+String.join("|", Main.tabSimFLOTANTES)+")";
        TABSIMBOLEANOS = "("+String.join("|", Main.tabSimBOLEANOS)+")";
        TABSIMCADENAS = "("+String.join("|", Main.tabSimCADENAS)+")";
        TIPOENTERO = "("+MENTERO+"|"+TABSIMENTEROS+")";
        TIPOFLOTANTE = "("+MFLOTANTE+"|"+TABSIMFLOTANTES+")";
        TIPOCADENA = "("+MCADENA+"|"+TABSIMCADENAS+")";
        TIPOBOLEANO = "("+MBOLEANO+"|"+TABSIMBOLEANOS+")";
        TIPOGENERAL = "("+TIPOENTERO+"|"+TIPOFLOTANTE+"|"+TIPOCADENA+"|"+TIPOBOLEANO+"|"+")";
        LEER = "LEER\\("+TABLASIMBOLOS+"\\)";
        IMPRIMIR = "IMPRIMIR\\(("+TABLASIMBOLOS+"|"+MCADENA+")\\)";
        COMPNUM = "(("+TIPOENTERO+"|"+TIPOFLOTANTE+")("+OPLOGNUM+")("+TIPOENTERO+"|"+TIPOFLOTANTE+"))";
        COMPBOLCAD = "(("+TIPOBOLEANO+"|"+TIPOCADENA+")"+OPLOGBOLCAD+"("+TIPOBOLEANO+"|"+TIPOCADENA+"))";
        COMPARACION = "("+COMPNUM+"|"+COMPBOLCAD+"|"+MBOLEANO+"|"+TABSIMBOLEANOS+")";
        COMPARACIONG = "("+TIPOGENERAL+OPLOGNUM+TIPOGENERAL+")";
        COMPARACIONCOMPUESTA = "("+COMPARACION+"(\\&|\\|)"+COMPARACION+")";
        COMPARACIONCOMPUESTAG = "("+COMPARACIONG+"(\\&|\\|)"+COMPARACIONG+")";
        SI = "SI\\(("+COMPARACION+"|"+COMPARACIONCOMPUESTA+")\\)\\{";
        SIG = "SI\\(("+COMPARACIONG+"|"+COMPARACIONCOMPUESTAG+")\\)\\{";
        DELOCONTRARIO = "\\}DELOCONTRARIO\\{";
        PARA = "PARA\\("+TABSIMENTEROS+"->("+TABSIMENTEROS+"|"+MENTERO+")\\)\\{";
        PARAG = "PARA\\("+TABLASIMBOLOS+"->"+TIPOGENERAL+"\\)\\{";
        MIENTRAS = "MIENTRAS\\("+COMPARACION+"\\)\\{";
        MIENTRASG = "MIENTRAS\\("+COMPARACIONG+"\\)\\{";
        ASIGNACIONENTERO = "("+TABSIMENTEROS+"=("+MENTERO+"|"+TABSIMENTEROS+"))";
        ASIGNACIONFLOTANTE = "("+TABSIMFLOTANTES+"=("+MFLOTANTE+"|"+TABSIMFLOTANTES+"))";
        ASIGNACIONBOLEANO = "("+TABSIMBOLEANOS+"=("+MBOLEANO+"|"+TABSIMBOLEANOS+"))";
        ASIGNACIONCADENA = "("+TABSIMCADENAS+"=("+MCADENA+"|"+TABSIMCADENAS+"))";
        ASIGNACION = "("+ASIGNACIONENTERO+"|"+ASIGNACIONFLOTANTE+"|"+ASIGNACIONBOLEANO+"|"+ASIGNACIONCADENA+")";
        ASIGNACIONG = TABLASIMBOLOS+"="+TIPOGENERAL;
        OPARITENTERO = TABSIMENTEROS+"=("+TABSIMENTEROS+"|"+MENTERO+")"+OPARITMETICOS+"("+TABSIMENTEROS+"|"+MENTERO+")";
        OPARITCADENA = TABSIMCADENAS+"=("+TABSIMCADENAS+"|"+MCADENA+")"+"\\+"+"("+TABSIMCADENAS+"|"+MCADENA+")";
        OPARITFLOTANTE = TABSIMFLOTANTES+"=("+TABSIMFLOTANTES+"|"+MFLOTANTE+")"+OPARITMETICOS+"("+TABSIMFLOTANTES+"|"+MFLOTANTE+")";
        OPARITMETICAG = TABLASIMBOLOS+"="+TIPOGENERAL+OPARITMETICOS+TIPOGENERAL;
        DECLARACIONVARIABLE = TIPOVARIABLES+"\\("+TABLASIMBOLOS+"\\)";
        FINSEGMENTO = "\\}";
        numLinea = "";
    
        boolean resultado = true;
        String auxiliar = "";
        
        for (String linea : Main.codigoLineas){
            int inicioLinea = linea.indexOf(';') + 1;
            auxiliar = linea.substring(inicioLinea);
            numLinea = linea.substring(0, inicioLinea - 1);
            
            String SENTENCIAS = "("+LEER+")|("+IMPRIMIR+")|("+SIG+")|("+PARAG+")|("+MIENTRASG+")|("+DELOCONTRARIO+")|("+OPARITMETICAG+")|("+ASIGNACIONG+")|("+DECLARACIONVARIABLE+")|("+FINSEGMENTO+")";
            if (!auxiliar.matches(SENTENCIAS)){
                resultado = false;
                System.out.println("[ET - 02] ERROR en la linea "+ numLinea + " -> "+auxiliar+": SENTENCIA INVALIDA");
            }
            SENTENCIAS = "("+SIG+")|("+PARAG+")|("+MIENTRASG+")|("+OPARITMETICAG+")|("+ASIGNACIONG+")";
            if (auxiliar.matches(SENTENCIAS)){
                if (errorTipo(auxiliar)) {
                    resultado = false;
                    System.out.println("[EM - 03] ERROR en la linea " + numLinea + " -> " + auxiliar + ": TIPO DE DATO INVALIDO");
                }
            }
        }
        return resultado;
    }

    public static boolean errorTipo(String linea){
        String SENTIPO = "("+OPARITFLOTANTE+")|("+SI+")|("+PARA+")|("+MIENTRAS+")|("+OPARITENTERO+")|("+OPARITCADENA+")|("+ASIGNACION+")";
        if (!linea.matches(SENTIPO)) return true;
        return false;
    }
}
