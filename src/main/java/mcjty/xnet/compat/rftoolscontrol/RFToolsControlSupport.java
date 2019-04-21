package mcjty.xnet.compat.rftoolscontrol;

import mcjty.rftoolscontrol.api.code.Opcode;
import mcjty.rftoolscontrol.api.parameters.Inventory;
import mcjty.rftoolscontrol.api.parameters.ParameterDescription;
import mcjty.rftoolscontrol.api.registry.IOpcodeRegistry;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.controller.TileEntityController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.function.Function;

import static mcjty.rftoolscontrol.api.code.IOpcodeRunnable.OpcodeResult.POSITIVE;
import static mcjty.rftoolscontrol.api.code.OpcodeOutput.SINGLE;
import static mcjty.rftoolscontrol.api.parameters.ParameterType.*;

public class RFToolsControlSupport {

    public static final Opcode DO_CHANNEL = Opcode.builder()
            .id("xnet:channel")
            .description(
                    TextFormatting.GREEN + "Operation: channel (XNet)",
                    "turn on or off an XNet channel")
            .opcodeOutput(SINGLE)
            .parameter(ParameterDescription.builder().name("controller").type(PAR_INVENTORY).description("XNet controller adjacent to (networked) block").build())
            .parameter(ParameterDescription.builder().name("channel").type(PAR_INTEGER).description("Channel number (starting at 0)").build())
            .parameter(ParameterDescription.builder().name("enabled").type(PAR_BOOLEAN).description("Enable or disable").build())
            .icon(0, 5, XNet.MODID + ":textures/gui/guielements.png")
            .runnable(((processor, program, opcode) -> {
                Inventory inv = processor.evaluateParameter(opcode, program, 0);
                int channel = processor.evaluateIntParameter(opcode, program, 1);
                if (channel < 0 || channel > 7) {
                    processor.log("Wrong channel!");
                    return POSITIVE;
                }
                boolean enable = processor.evaluateBoolParameter(opcode, program, 2);
                TileEntity te = processor.getTileEntityAt(inv);
                if (te instanceof TileEntityController) {
                    TileEntityController controller = (TileEntityController) te;
                    controller.getChannels()[channel].setEnabled(enable);
                }
                return POSITIVE;
            }))
            .build();

    public static class GetOpcodeRegistry implements Function<IOpcodeRegistry, Void> {
        @Nullable
        @Override
        public Void apply(IOpcodeRegistry registry) {
            registry.register(DO_CHANNEL);
            return null;
        }
    }
}
