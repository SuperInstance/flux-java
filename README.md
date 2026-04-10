# flux-java

FLUX bytecode VM in Java with two-pass assembler. Pure Java, no dependencies.

## Classes
- `FluxVM` — Bytecode interpreter (16 registers, stack, 16 opcodes)
- `Assembler` — Text to bytecode (labels, two-pass resolution)
- `FluxMain` — Demo (Factorial, Fibonacci)

## Building

```bash
javac src/main/java/com/superinstance/flux/*.java
java -cp src main.java.com.superinstance.flux.FluxMain
```
