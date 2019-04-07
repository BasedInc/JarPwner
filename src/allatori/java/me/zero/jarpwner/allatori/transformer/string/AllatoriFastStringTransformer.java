package me.zero.jarpwner.allatori.transformer.string;

import me.zero.jarpwner.asm.AsmUtils;
import me.zero.jarpwner.asm.search.Pattern;
import me.zero.jarpwner.asm.cl.ASMClassLoader;
import me.zero.jarpwner.asm.cl.IASMClassLoader;
import me.zero.jarpwner.transform.ITransformerContext;
import me.zero.jarpwner.transform.Transformer;
import me.zero.jarpwner.transform.TransformerMeta;
import me.zero.jarpwner.util.Utils;
import org.objectweb.asm.tree.*;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Brady
 * @since 4/3/2019
 */
@TransformerMeta(
        name = "Fast String Decrypter",
        desc = "Decrypts strings that were encrypted using the fast mode"
)
public class AllatoriFastStringTransformer extends Transformer {

    private static final Pattern PATTERN = Pattern.of(
            LDC,
            INVOKESTATIC
    );

    private static final List<Integer> OPCODES = Arrays.asList(
            ICONST_1,
            ICONST_2,
            ICONST_3,
            ICONST_4,
            ICONST_5,
            ILOAD,
            ALOAD,
            ISTORE,
            ASTORE,
            CASTORE,
            POP,
            POP2,
            DUP,
            DUP_X1,
            DUP_X2,
            SWAP,
            ISUB,
            ISHL,
            IXOR,
            IINC,
            I2C,
            IFLT,
            GOTO,
            ARETURN,
            INVOKEVIRTUAL,
            INVOKESPECIAL,
            NEW,
            NEWARRAY
    );

    private IASMClassLoader classLoader;
    private int succeeded;
    private int failed;
    private int decryptors;

    public AllatoriFastStringTransformer(ITransformerContext context) {
        super(context);
    }

    @Override
    public void setup() {
        this.classLoader = new ASMClassLoader();
    }

    @Override
    public void apply(ClassNode cn) {
        cn.methods.forEach(mn -> {
            if ((mn.access & ACC_STATIC) == 0 || !mn.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                return;
            }

            // Verify that the method has the gamer opcodes
            for (AbstractInsnNode insn : AsmUtils.iterable(mn.instructions)) {
                if (insn instanceof MethodInsnNode) {
                    if (!((MethodInsnNode) insn).owner.equals("java/lang/String"))
                        return;
                }

                if (insn instanceof TypeInsnNode) {
                    if (!((TypeInsnNode) insn).desc.equals("java/lang/String"))
                        return;
                }

                if (insn.getOpcode() >= 0 && !OPCODES.contains(insn.getOpcode())) {
                    return;
                }
            }

            var callsites = new ArrayList<EncryptedStringCallsite>();

            // Find callers
            this.context.getSource().getClasses().getAll().forEach((path, node) -> node.methods.forEach(nodeMn -> {
                PATTERN.find(nodeMn.instructions, Pattern.SearchFlags.IGNORE_ALL).forEach(slice -> {
                    var ldc = (LdcInsnNode) slice.getFrom();
                    if (!(ldc.cst instanceof String))
                        return;

                    var invoke = (MethodInsnNode) slice.getTo();
                    if (!invoke.owner.equals(cn.name) || !invoke.name.equals(mn.name) || !invoke.desc.equals(mn.desc))
                        return;

                    callsites.add(new EncryptedStringCallsite(nodeMn.instructions, ldc, invoke));
                });
            }));

            if (callsites.size() == 0)
                return;

            var className = "Decryptor" + ++decryptors;

            // Copy the decryption method
            var decryptorMethod = new MethodNode(ACC_PUBLIC | ACC_STATIC, "decrypt", "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            AsmUtils.stream(mn.instructions).forEach(decryptorMethod.instructions::add);

            // Setup the ClassNode
            var decryptorClass = new ClassNode();
            decryptorClass.visit(46, ACC_PUBLIC, className, null, "java/lang/Object", null);
            decryptorClass.methods.add(decryptorMethod);

            // Actually create a class from the ClassNode
            var clazz = classLoader.defineClass(decryptorClass);
            if (clazz == null) {
                this.failed += callsites.size();
                return;
            }

            // Try to get the reflect method
            var method = Utils.tryOrElseNull(() -> clazz.getMethod("decrypt", String.class));
            if (method == null) {
                this.failed += callsites.size();
                return;
            }

            // Go through all of the callsites and try to rewrite the instructions with the decrypted strings
            var cache = new HashMap<String, String>();
            callsites.forEach(callsite -> {
                var decrypted = cache.computeIfAbsent(callsite.getString(), encrypted ->
                        Utils.tryOrElseNull(() -> (String) method.invoke(null, encrypted)));

                if (decrypted == null) {
                    this.failed++;
                } else {
                    callsite.rewrite(decrypted);
                    this.succeeded++;
                }
            });
        });
    }

    @Override
    public boolean accepts(String path) {
        return true;
    }

    @Override
    public Collection<String> getInfo() {
        return Arrays.asList(
                String.format("Replaced %d decrypted string(s) with %d decryptor(s)", this.succeeded, this.decryptors),
                String.format("Failed to decrypt %d string(s)", this.failed)
        );
    }

    private static class EncryptedStringCallsite {

        /**
         * The instruction list that the callsite was found in
         */
        private final InsnList insns;

        /**
         * The string parameter given to the decryption method that is encrypted
         */
        private final LdcInsnNode ldc;

        /**
         * The decryption method invocation instruction
         */
        private final MethodInsnNode invoke;

        EncryptedStringCallsite(InsnList insns, LdcInsnNode ldc, MethodInsnNode invoke) {
            this.insns = insns;
            this.ldc = ldc;
            this.invoke = invoke;
        }

        /**
         * Rewrites the callsite to use the raw decrypted string rather than pass the encrypted one to the decryption method.
         *
         * @param decrypted The decrypted string
         */
        final void rewrite(String decrypted) {
            insns.remove(invoke);
            ldc.cst = decrypted;
        }

        final String getString() {
            return (String) ldc.cst;
        }
    }
}
