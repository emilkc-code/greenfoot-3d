import greenfoot.*;
public class Canvas extends World
{
public Canvas()
{
  super(1000, 1000, 1);

  // Add the Renderer for visibility and Main for running code
  addObject(new Renderer(getWidth(), getHeight()), getWidth() / 2, getHeight() / 2);
  addObject(new Main(1, 1), 0, 0);
}
}