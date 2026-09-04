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
        if (!wired && getWorld() != null) {
            java.util.List<Renderer> renderers = getWorld().getObjects(Renderer.class);
            if (!renderers.isEmpty()) {
                compositor.setRenderer(renderers.get(0));
                renderers.get(0).setCamera(camera);
                wired = true;
            }
        }
        control.setCamera(camera);
        
        control.controlCamera();
        compositor.composit();
    }
}