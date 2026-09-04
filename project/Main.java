import greenfoot.*;
public class Main extends Actor
{
private Compositor compositor = new Compositor();
private Control control = new Control();
private Camera camera = new Camera();

public Main(int width, int height) {
  setImage(new GreenfootImage(width, height));
}

private boolean wired = false;

public void act()
{

  // Check if we are in a world yet and if so we can use a variable to stop checking again
  if (!wired && getWorld() != null) {
    java.util.List<Renderer> renderers = getWorld().getObjects(Renderer.class);
    if (!renderers.isEmpty()) {

      // Here we set the Renderer for the Compositor
      compositor.setRenderer(renderers.get(0));

      // Here we set the camera to use for the Renderer
      renderers.get(0).setCamera(camera);
      wired = true;
    }
  }

  // Setting the same camera in Control
  control.setCamera(camera);

  // Calling our main methods
  control.controlCamera();
  compositor.composit();
}
}