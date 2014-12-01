package zmaster587.libVulpes.render;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class RenderHelper {
	
	/**
	 * 
	 * @param text text to render
	 * @param horizontalLetter max letters in horizontal direction
	 * @param vertLetters max letters in vertial direction
	 * @param font resource location of the font to use
	 */
	public static void renderText(TextPart text, int horizontalLetter, int vertLetters, ResourceLocation font) {
		
		StringTokenizer tokens = new StringTokenizer(text.text, "\n");
		Tessellator tess = Tessellator.instance;
		
		//tess.setColorRGBA(text.colorRGBA & 255, (text.colorRGBA << 8) & 255, (text.colorRGBA << 16) & 255, (text.colorRGBA << 24) & 255);
		
		GL11.glColor3ub((byte)((text.colorRGBA >> 16) & 255), (byte)((text.colorRGBA >> 8) & 255), (byte)(text.colorRGBA & 255));//, (byte)((text.colorRGBA >> 24) & 255));
		
		
		tess.startDrawingQuads();
		for(int line = 0; line < vertLetters && tokens.hasMoreTokens(); line++) {
			String token = tokens.nextToken();
			
			for(int i = 0 ; i < token.length() && i < horizontalLetter; i++) {
				tess.addVertexWithUV(text.offsetX + text.size*i, text.offsetY -text.size*line - text.size, 0, (8*(token.charAt(i) % 16) + (token.charAt(i) % 16))/145D, (1/145D) + (8*(1 + (token.charAt(i) / 16)) + (token.charAt(i)/16))/145D);
				tess.addVertexWithUV(text.offsetX + text.size*i + text.size, text.offsetY - text.size*line - text.size, 0, (8*(1 + (token.charAt(i) % 16)) + (token.charAt(i) % 16))/145D, (1/145D) + (8*(1 + (token.charAt(i) / 16)) + (token.charAt(i)/16))/145D);
				tess.addVertexWithUV(text.offsetX + text.size*i + text.size, text.offsetY - text.size*line, 0, (8*(1 + (token.charAt(i) % 16)) + (token.charAt(i) % 16))/145D, (8*(token.charAt(i) / 16) + (token.charAt(i)/16))/145D);
				tess.addVertexWithUV(text.offsetX + text.size*i, text.offsetY - text.size*line, 0, (8*(token.charAt(i) % 16) + (token.charAt(i) % 16))/145D,(8*(token.charAt(i) / 16) + (token.charAt(i)/16))/145D);
				
			}
		}
		tess.draw();
	}
}
