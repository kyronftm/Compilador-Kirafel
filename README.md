# Compilador-Kirafel
Compilador del lenguaje Kirafel.

# Estructura
El compilador cuenta con 5 partes en su estructura: 
  * Primero tenemos un lector de archivos con terminacion .krfl los cuales contienen el codigo a ejecutar.
  * En segundo lugar se encuentra un analizador lexico, el cual se encarga de tokenizar el codigo para identificar errores lexicos.
  * En tercer lugar el analizador sintactico, que determina la validad da la sintaxis en cada linea de codigo.
  * En cuarto lugar el analizador semantico, encargado de verificar una correcta estructuracion del codigo, es decir, que tenga coherencia.
  * En quinto y ultimo lugar, tenemos el generador de codigo, el cual como dice su nombre, se encarga generar un archivo .asm donde se traduce el codigo kirafel a ensamblador para poder utilizarlo en un procesador x86 de 16 bits con comandos DOS.

# Sentencias
El lenguaje cuenta con las sentencias basicas para programar como lo son:
  * IMPRIMIR
  * LEER
  * DECLARACION DE VARIABLE
  * ASIGNACION DE VALOR
  * OPERACIONES ARITMETICAS
  * SI
  * DE LO CONTRARIO
  * PARA
  * MIENTRAS

# Tipos-de-datos
Los tipos de datos que se pueden declarar en el lenguaje son:
  * ENTERO
  * FLOTANTE
  * BOLEANO
  * CADENA

# Tipos-de-operaciones-aritmeticas
Las operaciones aritmeticas solamente son simples, y son:
  * Suma +
  * Resta -
  * Producto *
  * Division /
  * Modulo %
  * Potenciacion ^

Tambien podemos realizar sumas de cadenas (+).

# Condiciones
Las condiciones utilizadas en los bloques SI y MIENTRAS pueden simples, es decir, solo un valor boleano que puede ser resultado de una comparacion, de una variable boleana o directamente el valor boleano VERDADERO o FALSO, o puede ser compuesta por dos valores boleanos separados por una operacion logica & (Y) o | (O).
