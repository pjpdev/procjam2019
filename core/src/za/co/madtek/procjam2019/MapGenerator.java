package za.co.madtek.procjam2019;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MapGenerator {

    private int mapWidth;
    private int mapHeight;

    private int[][] mapData;
    private int[][] chunkData;

    private int landmassModifier;

    private int tilesUsed;

    private Random rnd;

    private int tileSize = 16;
    private float mapX = 0;
    private float mapY = 0;

    private SpriteBatch batch;

    private Texture tilesetImg;

    private HashMap<String, TextureRegion[][]> layers;
    private ArrayList<TextureRegion> tiles;
    private TextureRegion[][] waterLayer;
    private TextureRegion[][] landLayer;

    public MapGenerator(int width, int height, int landmassModifier) {
        /*--------------------------------------------
          Create Map
         ---------------------------------------------*/

        // Initialize base map data
        this.mapWidth = width;
        this.mapHeight = height;

        this.mapData = new int[width][height];
        this.chunkData = new int[width][height];

        this.landmassModifier = landmassModifier;

        this.rnd = new Random();

        // Initialize Tilemap
        initTilemap();

    }

    public void render() {
        /*--------------------------------------------
          Render map
         ---------------------------------------------*/

        batch.begin();

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                batch.draw(waterLayer[x][y], mapX + (x * tileSize), mapY + (y * tileSize));
            }
        }

        batch.end();
    }

    public void generate() {
        /*--------------------------------------------
          Start generating map
         ---------------------------------------------*/

        // Clear mapData
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                mapData[x][y] = 0;
            }
        }

        // Set basic tile accounting parameters
        tilesUsed = 0;
        int maxTiles = (landmassModifier + 2) * 480;

        // Start generating chunks.
        while (tilesUsed < maxTiles) {
            generateChunk();
            mergeChunk();
            tilesUsed = calculateTiles();
        }

        processTilemap();

        /*System.out.println("--- Map Data ---");
        for (int x = 0; x < mapWidth; x++) {
            System.out.print("[");
            for (int y = 0; y < mapHeight; y++) {
                System.out.print(Integer.toString(mapData[x][y]) + ",");
            }
            System.out.print("]\n");
        }
        System.out.println("--- End Of Map Data ---");*/
    }

    public void updateRenderMatrix(Matrix4 projection) {
        /*--------------------------------------------
          Update renderer projection matrix
         ---------------------------------------------*/

        batch.setProjectionMatrix(projection);
    }

    private void generateChunk() {
        /*--------------------------------------------
          Generate a single chunk
         ---------------------------------------------*/

        // Clear chunk data.
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                chunkData[x][y] = 0;
            }
        }

        // Set basic parameters
        int min = 5;
        int maxX = mapWidth - 5;
        int maxY = mapHeight - 5;
        int posX = rnd.nextInt((maxX - min)+1) + min;
        int posY = rnd.nextInt((maxY - min) + 1) + min;
        int length = rnd.nextInt(64) + 1;

        // Start generating chunk
        while (length > 0) {
            // Fill tiles with land
            chunkData[posX][posY] = 1;
            chunkData[posX+1][posY] = 1;
            chunkData[posX][posY+1] = 1;

            // Select next direction
            int dir = rnd.nextInt(4); // 0 = N, 1 = E, 2 = S, 3 = W
            switch (dir) {
                case 0: {
                    posY += 1;
                    break;
                }
                case 1: {
                    posX += 1;
                    break;
                }
                case 2: {
                    posY -= 1;
                    break;
                }
                case 3: {
                    posX -= 1;
                }
            }

            // Check bounds.
            if ((posX < 5) || (posX > mapWidth-5) || (posY < 5) || (posY > mapHeight-5)) {
                length = -1;
            } else {
                length -= 1;
            }
        }

    }

    private void mergeChunk() {
        /*--------------------------------------------
          Merge chunk into map
         ---------------------------------------------*/

        for (int x = 0; x < mapWidth; x++){
            for (int y = 0; y < mapHeight; y++) {
                mapData[x][y] += chunkData[x][y];
            }
        }
    }

    private int calculateTiles() {
        /*--------------------------------------------
          Calculate tile count
         ---------------------------------------------*/

        int tempCount = 0;

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (mapData[x][y] > 0) tempCount += 1;
            }
        }

        return tempCount;
    }

    private void initTilemap() {
        /*--------------------------------------------
          Initialize the tilemap
         ---------------------------------------------*/

        batch = new SpriteBatch();

        // Load tilesets
        tilesetImg = new Texture("lpcnew.png");

        layers = new HashMap<String, TextureRegion[][]>();
        waterLayer = new TextureRegion[mapWidth][mapHeight];
        landLayer = new TextureRegion[mapWidth][mapHeight];

        tiles = new ArrayList<TextureRegion>();

        tiles.add(new TextureRegion(tilesetImg, 0, 0, 16, 16)); // Water 0
        tiles.add(new TextureRegion(tilesetImg, 32, 64, 16, 16)); // Land 1
        tiles.add(new TextureRegion(tilesetImg, 16, 48, 16, 16)); // NW 2
        tiles.add(new TextureRegion(tilesetImg, 32, 48, 16, 16)); // N 3
        tiles.add(new TextureRegion(tilesetImg, 48, 48, 16, 16)); // NE 4
        tiles.add(new TextureRegion(tilesetImg, 48, 64, 16, 16)); // E 5
        tiles.add(new TextureRegion(tilesetImg, 48, 80, 16, 16)); // SE 6
        tiles.add(new TextureRegion(tilesetImg, 32, 80, 16, 16)); // S 7
        tiles.add(new TextureRegion(tilesetImg, 16, 80, 16, 16)); // SW 8
        tiles.add(new TextureRegion(tilesetImg, 16, 64, 16, 16)); // W 9
        tiles.add(new TextureRegion(tilesetImg, 64, 0, 16, 16)); // 10
        tiles.add(new TextureRegion(tilesetImg, 64, 16, 16, 16)); // 11
    }

    private void processTilemap() {
        /*--------------------------------------------
          Process the tile map
         ---------------------------------------------*/

        //TextureRegion[][] waterLayer = new TextureRegion[mapWidth][mapHeight];
        //TextureRegion[][] landLayer = new TextureRegion[mapWidth][mapHeight];


        // Process mapData
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (mapData[x][y] == 0) {
                    waterLayer[x][y] = tiles.get(0);
                }
                if (mapData[x][y] >= 1) {
                    // Check neigbours

                    boolean[] dir = {false, false, false, false};

                    //North
                    if (y+1 < mapHeight && mapData[x][y+1] == 0) {
                        dir[0] = true;
                    }

                    //East
                    if (x+1 < mapWidth && mapData[x+1][y] == 0) {
                        dir[1] = true;
                    }

                    //South
                    if (y-1 > 0 && mapData[x][y-1] == 0) {
                        dir[2] = true;
                    }

                    //West
                    if (x-1 > 0 && mapData[x-1][y] == 0) {
                        dir[3] = true;
                    }

                    if (dir[0] && !dir[1] && !dir[2] && !dir[3]) {
                        waterLayer[x][y] = tiles.get(1);
                        waterLayer[x][y+1] = tiles.get(3);
                    } else if (dir[0] && dir[1] && !dir[2] && !dir[3]) {
                        waterLayer[x][y] = tiles.get(1);
                        waterLayer[x+1][y+1] = tiles.get(4);
                    } else if (!dir[0] && dir[1] && !dir[2] && !dir[3]) {
                        waterLayer[x][y] = tiles.get(5);
                    } else if (!dir[0] && dir[1] && dir[2] && !dir[3]) {
                        waterLayer[x][y] = tiles.get(6);
                    } else if (!dir[0] && !dir[1] && dir[2] && !dir[3]) {
                        waterLayer[x][y] = tiles.get(7);
                    } else if (!dir[0] && !dir[1] && dir[2] && dir[3]) {
                        waterLayer[x][y] = tiles.get(8);
                    } else if (!dir[0] && !dir[1] && !dir[2] && dir[3]) {
                        waterLayer[x][y] = tiles.get(9);
                    } else if (dir[0] && !dir[1] && !dir[2] && dir[3]) {
                        waterLayer[x][y] = tiles.get(9);
                    } else if (dir[0] && dir[1] && !dir[2] && dir[3]) {
                        waterLayer[x][y] = tiles.get(10);
                    } else if (!dir[0] && dir[1] && !dir[2] && dir[3]) {
                        waterLayer[x][y] = tiles.get(11);
                    } else {
                        waterLayer[x][y] = tiles.get(1);
                    }
                }
            }
        }

        //layers.put("Water", waterLayer);
        //layers.put("Land", landLayer);

    }
}
