package cullycross.airhockeygame.objects;

import java.util.List;

import cullycross.airhockeygame.data.VertexArray;
import cullycross.airhockeygame.programs.ColorShaderProgram;
import cullycross.airhockeygame.utils.Geometry;

/**
 * Created by cullycross on 2/18/15.
 */
public class Puck {

    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;

    private final VertexArray mVertexArray;
    private final List<ObjectBuilder.DrawCommand> mDrawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPuck(
                new Geometry.Cylinder(new Geometry.Point(0f, 0f, 0f), radius, height),
                numPointsAroundPuck
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
