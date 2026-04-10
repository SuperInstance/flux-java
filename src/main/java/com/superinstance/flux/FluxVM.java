package com.superinstance.flux;

/**
 * FLUX Bytecode Virtual Machine — Java Implementation
 * 
 * A clean-room implementation of the FLUX VM in Java.
 * Opcodes: MOV, MOVI, IADD, ISUB, IMUL, IDIV, INC, DEC,
 *          CMP, JZ, JNZ, JMP, PUSH, POP, LOAD, STORE, HALT
 */
public class FluxVM {
    // Opcodes
    public static final int HALT  = 0x80;
    public static final int MOV   = 0x01;
    public static final int MOVI  = 0x2B;
    public static final int IADD  = 0x08;
    public static final int ISUB  = 0x09;
    public static final int IMUL  = 0x0A;
    public static final int IDIV  = 0x0B;
    public static final int INC   = 0x0E;
    public static final int DEC   = 0x0F;
    public static final int CMP   = 0x2D;
    public static final int JZ    = 0x2E;
    public static final int JNZ   = 0x06;
    public static final int JMP   = 0x07;
    public static final int PUSH  = 0x10;
    public static final int POP   = 0x11;
    
    private int[] gp = new int[16];   // General purpose registers
    private int pc = 0;                // Program counter
    private boolean halted = false;
    private int cycles = 0;
    private int[] stack = new int[1024];
    private int sp = 0;
    private byte[] bytecode;
    
    public FluxVM(byte[] bytecode) {
        this.bytecode = bytecode;
    }
    
    private int u8() { return bytecode[pc++] & 0xFF; }
    private int i16() { 
        int lo = bytecode[pc++] & 0xFF;
        int hi = bytecode[pc++] & 0xFF;
        return (short)((hi << 8) | lo);
    }
    
    public int execute() {
        halted = false;
        cycles = 0;
        while (!halted && pc < bytecode.length && cycles < 10_000_000) {
            int op = u8();
            cycles++;
            switch (op) {
                case HALT: halted = true; break;
                case MOV: { int d=u8(), s=u8(); gp[d]=gp[s]; break; }
                case MOVI: { int d=u8(); int v=i16(); gp[d]=v; break; }
                case IADD: { int d=u8(),a=u8(),b=u8(); gp[d]=gp[a]+gp[b]; break; }
                case ISUB: { int d=u8(),a=u8(),b=u8(); gp[d]=gp[a]-gp[b]; break; }
                case IMUL: { int d=u8(),a=u8(),b=u8(); gp[d]=gp[a]*gp[b]; break; }
                case IDIV: { int d=u8(),a=u8(),b=u8(); gp[d]=gp[a]/gp[b]; break; }
                case INC: { int d=u8(); gp[d]++; break; }
                case DEC: { int d=u8(); gp[d]--; break; }
                case CMP: { int a=u8(),b=u8(); gp[13]=(gp[a]==gp[b])?0:(gp[a]<gp[b])?-1:1; break; }
                case JZ:  { int d=u8(); int off=i16(); if(gp[d]==0) pc+=off; break; }
                case JNZ: { int d=u8(); int off=i16(); if(gp[d]!=0) pc+=off; break; }
                case JMP: { int off=i16(); pc+=off; break; }
                case PUSH:{ int d=u8(); stack[sp++]=gp[d]; break; }
                case POP: { int d=u8(); gp[d]=stack[--sp]; break; }
                default: throw new RuntimeException("Unknown opcode: 0x" + Integer.toHexString(op));
            }
        }
        return cycles;
    }
    
    public int readReg(int i) { return gp[i]; }
    public void writeReg(int i, int v) { gp[i] = v; }
    public int getCycles() { return cycles; }
    public boolean isHalted() { return halted; }
}
