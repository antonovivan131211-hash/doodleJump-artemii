package io.github.some_example_name.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextView extends View {
    protected BitmapFont font;
    protected String text;

    public TextView(BitmapFont font, float x, float y) {
        super(x, y,0 ,0);
        this.font = font;
    }

    public TextView(BitmapFont font, float x, float y, String text) {
        this(font, x, y);
        this.text = text;

        GlyphLayout glyphLayout = new GlyphLayout(font, text);
        width = glyphLayout.width;
        height = glyphLayout.height;
    }
    public void setText(String text) {
        this.text = text;
        GlyphLayout glyphLayout = new GlyphLayout(font, text);
        width = glyphLayout.width;
        height = glyphLayout.height;
    }
    public void draw(SpriteBatch batch) {
        font.draw(batch, text, x, y);
    }
    public void dispose() {
        font.dispose();
    }
    @Override
    public boolean isHit(float tx, float ty) {
        return tx >=x && tx <= x + width && ty <=y && ty >= y - height;
    }
}
