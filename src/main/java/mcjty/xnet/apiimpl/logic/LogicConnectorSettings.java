package mcjty.xnet.apiimpl.logic;

import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.apiimpl.AbstractConnectorSettings;
import mcjty.xnet.blocks.controller.gui.GuiController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogicConnectorSettings extends AbstractConnectorSettings {

    public static final String TAG_SPEED = "speed";

    public static final int SENSORS = 4;

    private List<Sensor> sensors = null;

    private int colors;         // Current colormask
    private int speed = 2;

    public LogicConnectorSettings(boolean advanced, @Nonnull EnumFacing side) {
        super(advanced, side);
        sensors = new ArrayList<>(SENSORS);
        for (int i = 0 ; i < SENSORS ; i++) {
            sensors.add(new Sensor(i));
        }
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setColorMask(int colors) {
        this.colors = colors;
    }

    public int getColorMask() {
        return colors;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        return new IndicatorIcon(GuiController.iconGuiElements, 26, 70, 13, 10);
    }

    @Nullable
    @Override
    public String getIndicator() {
        return null;
    }

    @Override
    public boolean isEnabled(String tag) {
        if (tag.equals(TAG_FACING)) {
            return isAdvanced();
        }
        if (tag.equals(TAG_SPEED)) {
            return true;
        }
        for (Sensor sensor : sensors) {
            if (sensor.isEnabled(tag)) {
                return true;
            }
        }

        return false;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public void createGui(IEditorGui gui) {
        String[] speeds;
        if (isAdvanced()) {
            speeds = new String[] { "10", "20", "60", "100", "200" };
        } else {
            speeds = new String[] { "20", "60", "100", "200" };
        }
        sideGui(gui);
        colorsGui(gui);
        redstoneGui(gui);
        gui.nl()
                .choices(TAG_SPEED, "Number of ticks for each check", Integer.toString(speed * 10), speeds)
                .nl();
        for (Sensor sensor : sensors) {
            sensor.createGui(gui);
        }
    }

    @Override
    public void update(Map<String, Object> data) {
        super.update(data);
        String facing = (String) data.get(TAG_FACING);
        speed = Integer.parseInt((String) data.get(TAG_SPEED)) / 10;
        if (speed == 0) {
            speed = 2;
        }
        for (Sensor sensor : sensors) {
            sensor.update(data);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        speed = tag.getInteger("speed");
        if (speed == 0) {
            speed = 2;
        }
        colors = tag.getInteger("colors");
        for (Sensor sensor : sensors) {
            sensor.readFromNBT(tag);
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("speed", speed);
        tag.setInteger("colors", colors);
        for (Sensor sensor : sensors) {
            sensor.writeToNBT(tag);
        }
    }

}
