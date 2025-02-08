package fun.jiucai.common.protocol.sdiffusion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StableDiffusionParameters {

    private String prompt;
    private String sampler_index;

    private int seed;
    private int steps;
    private int cfg_scale;
    private int batch_size;
    private int width;
    private int height;

    private boolean restore_faces;
    private boolean tiling;

}


/**
 * {
 *     "parameters": {
 *         "enable_hr": false,
 *         "denoising_strength": 0,
 *         "firstphase_width": 0,
 *         "firstphase_height": 0,
 *         "hr_scale": 2,
 *         "hr_upscaler": null,
 *         "hr_second_pass_steps": 0,
 *         "hr_resize_x": 0,
 *         "hr_resize_y": 0,
 *         "hr_sampler_name": null,
 *         "hr_prompt": "",
 *         "hr_negative_prompt": "",
 *         "prompt": "girl",
 *         "styles": null,
 *         "seed": 2,
 *         "subseed": -1,
 *         "subseed_strength": 0,
 *         "seed_resize_from_h": -1,
 *         "seed_resize_from_w": -1,
 *         "sampler_name": null,
 *         "batch_size": 1,
 *         "n_iter": 1,
 *         "steps": 105,
 *         "cfg_scale": 7,
 *         "width": 768,
 *         "height": 1024,
 *         "restore_faces": false,
 *         "tiling": false,
 *         "do_not_save_samples": false,
 *         "do_not_save_grid": false,
 *         "negative_prompt": "",
 *         "eta": null,
 *         "s_min_uncond": 0,
 *         "s_churn": 0,
 *         "s_tmax": null,
 *         "s_tmin": 0,
 *         "s_noise": 1,
 *         "override_settings": null,
 *         "override_settings_restore_afterwards": true,
 *         "script_args": [],
 *         "sampler_index": "Euler",
 *         "script_name": null,
 *         "send_images": true,
 *         "save_images": false,
 *         "alwayson_scripts": {}
 *     }
 * }
 */