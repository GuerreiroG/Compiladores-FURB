.assembly extern mscorlib {}
.assembly _exemplo{}
.module _exemplo.exe

.class public _exemplo{
   .method static public void _principal(){
   .entrypoint

ldc.i8 3
conv.r8
conv.i8
call void [mscorlib]System.Console::WriteLine(int64)
ldc.r8 3.5
call void [mscorlib]System.Console::WriteLine(float64)
   ret
 }
}

