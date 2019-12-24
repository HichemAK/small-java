;;Assemble and link with
;nasm -fwin32 hello.asm
;gcc -o hello hello.obj

global _main 
extern _scanf 
extern _printf     

segment .text

_main:

   push msg
   call _printf
   pop eax

   push id  ; address of number1 (second parameter)
   push formatin ; arguments are pushed right to left (first parameter)
   call _scanf
   add esp, 8 


   push id
   call _printf
   add esp,4              

   ret
   
segment .data

    msg: db "Enter your ID", 0xA, 0xD, 0  ; note the null terminator.
    formatin: db "%s", 0                  ; for scanf.
	id db 10