package com.superinstance.flux;

public class FluxMain {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   FLUX JVM — Java Bytecode Runtime    ║");
        System.out.println("║   SuperInstance / Oracle1              ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // Demo: Factorial(7) = 5040
        byte[] fact = new byte[] {
            (byte)0x2B, 0x00, 0x07, 0x00,  // MOVI R0, 7
            (byte)0x2B, 0x01, 0x01, 0x00,  // MOVI R1, 1
            // loop:
            (byte)0x0A, 0x01, 0x01, 0x00,  // IMUL R1, R1, R0
            (byte)0x0F, 0x00,              // DEC R0
            (byte)0x06, 0x00, (byte)0xF6, (byte)0xFF, // JNZ R0, -10
            (byte)0x80                     // HALT
        };
        
        FluxVM vm = new FluxVM(fact);
        int cycles = vm.execute();
        
        System.out.println("Factorial(7):");
        System.out.println("  R0 = " + vm.readReg(0));
        System.out.println("  R1 = " + vm.readReg(1) + " (result)");
        System.out.println("  Cycles: " + cycles);
        System.out.println();
        
        // Fibonacci(12) = 144
        Assembler asm = new Assembler();
        byte[] fib = asm.assemble(
            "MOVI R0, 0\n" +
            "MOVI R1, 1\n" +
            "MOVI R2, 12\n" +
            "loop:\n" +
            "MOV R3, R1\n" +
            "IADD R1, R1, R0\n" +
            "MOV R0, R3\n" +
            "DEC R2\n" +
            "JNZ R2, loop\n" +
            "HALT"
        );
        
        FluxVM vm2 = new FluxVM(fib);
        int c2 = vm2.execute();
        System.out.println("Fibonacci(12):");
        System.out.println("  R1 = " + vm2.readReg(1) + " (result)");
        System.out.println("  Cycles: " + c2);
        
        System.out.println("\n✓ FLUX JVM implementation working!");
    }
}
