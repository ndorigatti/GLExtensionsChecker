package it.dorigatti.android.glchecker;

import android.app.Activity;
import android.opengl.ETC1Util;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CheckerActivity extends Activity {

    private GLSurfaceView mGLView;
    private TextView glVersionTv, glRendererTv, glExtensionsTv;
    private TextView glExtensionsEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        mGLView = new GLSurfaceView(this);
        mGLView.setRenderer(new GraphicChooserRenderer(width, height));
        //setContentView(mGLView);
        LinearLayout ll = (LinearLayout) findViewById(R.id.glLayout);
        ll.addView(mGLView);
        glVersionTv = (TextView) findViewById(R.id.gl_version_text);
        glRendererTv = (TextView) findViewById(R.id.gl_renderer_text);
        glExtensionsTv = (TextView) findViewById(R.id.gl_extensions_text);
        glExtensionsEnabled = (TextView) findViewById(R.id.gl_extensions_available_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.checker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    class GraphicChooserRenderer implements GLSurfaceView.Renderer {

        final int _width, _height;

        public GraphicChooserRenderer(int width, int height) {
            _width = width;
            _height = height;
        }

        public void determineGraphicSupport(GL10 gl) {

            final String extensions = gl.glGetString(GL10.GL_EXTENSIONS);
            final String version = GLES10.glGetString(GL10.GL_VERSION);
            final String renderer = gl.glGetString(GL10.GL_RENDERER);

            Log.w("GLExtensions", "Extensions: " + extensions);
            Log.w("GLVersion", "Version: " + version);
            Log.w("GLRenderer", "Renderer: " + renderer);
            // GL_EXT_debug_marker GL_OES_blend_func_separate GL_OES_blend_equation_separate GL_OES_blend_subtract
            // GL_OES_byte_coordinates GL_OES_compressed_paletted_texture GL_OES_point_size_array GL_OES_point_sprite
            // GL_OES_single_precision GL_OES_stencil_wrap GL_OES_texture_env_crossbar GL_OES_texture_mirored_repeat
            // GL_OES_EGL_image GL_OES_element_index_uint GL_OES_draw_texture GL_OES_texture_cube_map GL_OES_draw_texture
            // GL_OES_read_format GL_OES_framebuffer_object GL_OES_depth24 GL_OES_depth32 GL_OES_fbo_render_mipmap
            // GL_OES_rgb8_rgba8 GL_OES_stencil1 GL_OES_stencil4 GL_OES_stencil8
            // GL_OES_packed_depth_stencil GL_EXT_texture_format_BGRA8888 GL_APPLE_texture_format_BGRA8888
            // GL_OES_compressed_ETC1_RGB8_texture
            String enabledCompressions = "";
            if (extensions.contains("GL_IMG_texture_compression_pvrtc")) {
                //Use PVR compressed textures
                Log.w("Ext", "Device supports PVR");
                enabledCompressions += "Device supports PVR\n";
            }
            if (extensions.contains("GL_AMD_compressed_ATC_texture") ||
                    extensions.contains("GL_ATI_texture_compression_atitc")) {
                //Load ATI Textures
                Log.w("Ext", "Device supports ATI");
                enabledCompressions += "Device supports ATI\n";
            }
            if (extensions.contains("GL_OES_texture_compression_S3TC") ||
                    extensions.contains("GL_EXT_texture_compression_s3tc")) {
                //Use DTX Textures
                Log.w("Ext", "Device supports S3TC");
                enabledCompressions += "Device supports S3TC\n";
            }
            if (extensions.contains("GL_EXT_texture_compression_dxt1")) {
                //Use DTX1 Textures
                Log.w("Ext", "Device supports DXT1");
                enabledCompressions += "Device supports DXT1\n";
            }
            if (extensions.contains("GL_EXT_texture_compression_dxt3")) {
                //Use DTX3 Textures
                Log.w("Ext", "Device supports DXT3");
                enabledCompressions += "Device supports DXT3\n";
            }
            if (extensions.contains("GL_EXT_texture_compression_dxt5")) {
                //Use DTX5 Textures
                Log.w("Ext", "Device supports DXT5");
                enabledCompressions += "Device supports DXT5\n";
            }
            if (extensions.contains("GL_AMD_compressed_3DC_texture")) {
                //Use 3DC Texture
                Log.w("Ext", "Device supports 3DC textures");
                enabledCompressions += "Device supports 3DC textures\n";
            }
            if (ETC1Util.isETC1Supported()) {
                //Use ETC1
                Log.w("Ext", "Device supports ETC1 Rgb8 textures");
                enabledCompressions += "Device supports ETC1 Rgb8 textures\n";
            }
            final String enabComprFinal = enabledCompressions;
            CheckerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    glExtensionsTv.setText("Extensions: " + extensions);
                    glVersionTv.setText("Version: " + version);
                    glRendererTv.setText("Renderer: " + renderer);
                    glExtensionsEnabled.setText("Texture Compression Supported: " + enabComprFinal);
                }
            });
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            determineGraphicSupport(gl);
        }

        public void onSurfaceChanged(GL10 gl, int w, int h) {
            gl.glViewport(0, 0, w, h);
        }

        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        }
    }
}
