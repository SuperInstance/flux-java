package com.superinstance.flux;

import java.util.*;

/**
 * FLUX Assembler — text to bytecode.
 * Two-pass: collect labels, emit bytecode.
 */
public class Assembler {
    private Map<String, Integer> labels = new HashMap<>();
    private Map<String, Integer> forwardRefs = new HashMap<>();
    
    public byte[] assemble(String source) {
        // Pass 1: calculate sizes and collect labels
        String[] lines = source.split("\n");
        List<String[]> parsed = new ArrayList<>();
        int offset = 0;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("#")) continue;
            if (line.startsWith(".") || line.endsWith(":")) {
                String label = line.replace(":", "").trim();
                labels.put(label, offset);
                continue;
            }
            String[] parts = line.split("[,\\s]+");
            parsed.add(parts);
            offset += instructionSize(parts);
        }
        
        // Pass 2: emit bytecode
        List<Byte> bc = new ArrayList<>();
        for (String[] parts : parsed) {
            emitInstruction(parts, bc);
        }
        
        byte[] result = new byte[bc.size()];
        for (int i = 0; i < bc.size(); i++) result[i] = bc.get(i);
        return result;
    }
    
    private int instructionSize(String[] parts) {
        String op = parts[0].toUpperCase();
        if (op.equals("HALT") || op.equals("NOP")) return 1;
        if (op.equals("INC") || op.equals("DEC") || op.equals("PUSH") || op.equals("POP")) return 2;
        if (op.equals("MOV")) return 3;
        if (op.equals("MOVI") || op.equals("JZ") || op.equals("JNZ") || op.equals("JMP")) return 4;
        return 4; // IADD, ISUB, IMUL, IDIV
    }
    
    private void emitInstruction(String[] parts, List<Byte> bc) {
        String op = parts[0].toUpperCase();
        switch (op) {
            case "HALT": bc.add((byte)0x80); break;
            case "MOV": bc.add((byte)0x01); emitReg(parts[1],bc); emitReg(parts[2],bc); break;
            case "MOVI": bc.add((byte)0x2B); emitReg(parts[1],bc); emitImm(parts[2],bc); break;
            case "IADD": bc.add((byte)0x08); emitReg(parts[1],bc); emitReg(parts[2],bc); emitReg(parts[3],bc); break;
            case "ISUB": bc.add((byte)0x09); emitReg(parts[1],bc); emitReg(parts[2],bc); emitReg(parts[3],bc); break;
            case "IMUL": bc.add((byte)0x0A); emitReg(parts[1],bc); emitReg(parts[2],bc); emitReg(parts[3],bc); break;
            case "IDIV": bc.add((byte)0x0B); emitReg(parts[1],bc); emitReg(parts[2],bc); emitReg(parts[3],bc); break;
            case "INC": bc.add((byte)0x0E); emitReg(parts[1],bc); break;
            case "DEC": bc.add((byte)0x0F); emitReg(parts[1],bc); break;
            case "JNZ": bc.add((byte)0x06); emitReg(parts[1],bc); emitLabelImm(parts[2],bc); break;
            case "JZ": bc.add((byte)0x2E); emitReg(parts[1],bc); emitLabelImm(parts[2],bc); break;
            case "JMP": bc.add((byte)0x07); emitLabelImm(parts[1],bc); break;
            case "PUSH": bc.add((byte)0x10); emitReg(parts[1],bc); break;
            case "POP": bc.add((byte)0x11); emitReg(parts[1],bc); break;
        }
    }
    
    private void emitReg(String r, List<Byte> bc) {
        bc.add((byte)Integer.parseInt(r.replace("R","").replace("r","")));
    }
    private void emitImm(String v, List<Byte> bc) {
        short val = Short.parseShort(v);
        bc.add((byte)(val & 0xFF));
        bc.add((byte)((val >> 8) & 0xFF));
    }
    private void emitLabelImm(String v, List<Byte> bc) {
        if (labels.containsKey(v)) {
            // Calculate relative offset
            int target = labels.get(v);
            int current = bc.size() + 2; // after the 2 bytes we're about to write
            short off = (short)(target - current - 2); // adjust for instruction size
            bc.add((byte)(off & 0xFF));
            bc.add((byte)((off >> 8) & 0xFF));
        } else {
            emitImm(v, bc);
        }
    }
}
