import greenfoot.*;
public class Canvas extends World
{
    public Canvas()
    {
        super(1000, 1000, 1);
        addObject(new Renderer(getWidth(), getHeight()), getWidth() / 2, getHeight() / 2);
        addObject(new Main(1, 1), 0, 0);
    }
}