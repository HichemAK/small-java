grammar Small_Java;

r : import_bib class_declare;
import_bib : (IMPORT_KW bibs SEMICOLON)*;

class_declare : modif CLASS_KW IDF ACC_B class_content ACC_E;
class_content : vars_declare main;
vars_declare : var_declare*;
main : MAIN_KW ACC_B instruction* ACC_E;
instruction : assign
            | if_cond
            | read
            | write;

assign : IDF ASSIGN (exp | string) SEMICOLON;
if_cond : IF_KW PAR_B exp PAR_E THEN_KW ACC_B instruction* ACC_E (ELSE_KW ACC_B instruction* ACC_E)?;
read : IN_KW PAR_B format COMMA IDF PAR_E SEMICOLON;
write : OUT_KW PAR_B (IDF | string) (COMMA exp)* PAR_E SEMICOLON;


exp : factor
	(PLUS factor | MINUS factor)*;

factor  : v
	(MUL v  | DIV v )*;

v : (INT | FLOAT)
    | IDF
	| PAR_B exp PAR_E;

var_declare : type (IDF COMMA)* IDF SEMICOLON;

bibs : BIB_IO
     | BIB_LANG;
type : TYPE_INT
     | TYPE_FLOAT
     | TYPE_STRING;
modif : MODIF_PUBLIC
      | MODIF_PROTECTED
      | ;
format : FORMAT_INT
       | FORMAT_FLOAT
       | FORMAT_STRING;
string : STRING;


IMPORT_KW : 'import';
CLASS_KW : 'class_SJ';
MAIN_KW : 'main_SJ';

IN_KW : 'In_SJ';
OUT_KW : 'Out_SJ';

IF_KW : 'if';
ELSE_KW : 'else';
THEN_KW : 'then';

PLUS : '+';
MINUS : '-';
MUL : '*';
DIV : '/';

ASSIGN : ':=';

PAR_B : '(';
PAR_E : ')';

ACC_B : '{';
ACC_E : '}';

COMMA : ',';
SEMICOLON : ';';

FORMAT_INT : '%d';
FORMAT_FLOAT : '%f';
FORMAT_STRING : '%s';

MODIF_PUBLIC : 'public';
MODIF_PROTECTED : 'protected';

BIB_LANG : 'Small_Java.lang';
BIB_IO : 'Small_Java.io';

TYPE_INT : 'int_SJ';
TYPE_FLOAT : 'float_SJ';
TYPE_STRING : 'string_SJ';



INT : [-]?[0-9]+;
FLOAT : INT'.'[0-9]+;

NUMBER : INT | FLOAT;

IDF : [A-Za-z]{1,8};

STRING : '"'.*?'"';
WS : [ \r\t\n] -> skip;