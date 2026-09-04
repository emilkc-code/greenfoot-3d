public class Compositor
{
    private Cube cube = new Cube();
    private Teapot teapot = new Teapot();
    private StanfordBunny stanfordBunny = new StanfordBunny();
    private LostEmpire lostEmpire = new LostEmpire();
    private XYZRGBDragon xYZRGBDragon = new XYZRGBDragon();
    private Renderer renderer;
    
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }
    
    private void queueMesh(Meshes mesh) {
        renderer.queueMesh(
            mesh.getPosition(),
            mesh.getRotation(),
            mesh.getScale(),
            mesh.getVertices(),
            mesh.getTriangleIndices(),
            mesh.getTriangleUVs(),
            mesh.getTexture()
        );
    }
    
    public void composit() {
        if (renderer == null) { return; }
    
        renderer.clearQueue();
        
        cube.setPosition(new float[] { 0, 0, -2000 });
        //queueMesh(cube);
        
        teapot.setPosition(new float[] { 2000, 500, -2000 });
        queueMesh(teapot);
        
        stanfordBunny.setPosition(new float[] { 4000, 500, -2000 });
        //queueMesh(stanfordBunny);
        
        lostEmpire.setPosition(new float[] { 0, 600, -2000 });
        //queueMesh(lostEmpire);
        
        xYZRGBDragon.setPosition(new float[] { 0, 600, -2000 });
        //queueMesh(xYZRGBDragon);
        
        renderer.drawScene();
    }
}