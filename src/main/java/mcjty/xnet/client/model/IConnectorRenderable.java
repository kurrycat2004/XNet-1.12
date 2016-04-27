package mcjty.xnet.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 27-4-2016.
 */
public interface IConnectorRenderable {

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getTexture(boolean front);

    @SideOnly(Side.CLIENT)
    public boolean renderFront();

}
