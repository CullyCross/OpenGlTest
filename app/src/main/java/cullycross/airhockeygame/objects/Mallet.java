package cullycross.airhockeygame.objects;

import java.util.List;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

import cullycross.airhockeygame.Constants;
import cullycross.airhockeygame.data.VertexArray;
import cullycross.airhockeygame.programs.ColorShaderProgram;
import cullycross.airhockeygame.utils.Geometry;

/**
 * Created by cullycross on 2/17/15.
 */
public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius;
    public final float height;

    private final VertexArray mVertexArray;
    private final List<ObjectBuilder.DrawCommand> mDrawList;

    public Mallet(float radius, float height, int numPointsAroundMallet) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(
                new Geometry.Point(0f, 0f, 0f), radius, height, numPointsAroundMallet
        );

        this.radius = radius;
        this.height = height;

        mVertexArray = new VertexArray(generatedData.vertexData);
        mDrawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram) {
        mVertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for(ObjectBuilder.DrawCommand drawCommand : mDrawList) {
            drawCommand.draw();
        }
    }

}
