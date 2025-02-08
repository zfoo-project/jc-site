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
public class StableDiffusionRequest {

    private String sampler_name;
    private String prompt;
    private String negative_prompt;
    private long seed;
    private int steps;
    private int batch_size;
    private int width;
    private int height;
    private int cfg_scale;

    public StableDiffusionRequest(String sampler_name, String prompt, String negative_prompt, long seed, int steps, int batch_size, int width, int height, int cfg_scale) {
        this.sampler_name = sampler_name;
        this.prompt = prompt;
        this.negative_prompt = negative_prompt;
        this.seed = seed;
        this.steps = steps;
        this.batch_size = batch_size;
        this.width = width;
        this.height = height;
        this.cfg_scale = cfg_scale;
    }

    private boolean enable_hr;
    private int hr_scale;
    private double denoising_strength;
    private String hr_upscaler;
}


/**
 * {
 * "enable_hr": false,
 * "denoising_strength": 0,
 * "firstphase_width": 0,
 * "firstphase_height": 0,
 * "hr_scale": 2,
 * "hr_upscaler": "string",
 * "hr_second_pass_steps": 0,
 * "hr_resize_x": 0,
 * "hr_resize_y": 0,
 * "hr_sampler_name": "string",
 * "hr_prompt": "",
 * "hr_negative_prompt": "",
 * "prompt": "",
 * "styles": [
 * "string"
 * ],
 * "seed": -1,
 * "subseed": -1,
 * "subseed_strength": 0,
 * "seed_resize_from_h": -1,
 * "seed_resize_from_w": -1,
 * "sampler_name": "string",
 * "batch_size": 1,
 * "n_iter": 1,
 * "steps": 50,
 * "cfg_scale": 7,
 * "width": 512,
 * "height": 512,
 * "restore_faces": false,
 * "tiling": false,
 * "do_not_save_samples": false,
 * "do_not_save_grid": false,
 * "negative_prompt": "string",
 * "eta": 0,
 * "s_min_uncond": 0,
 * "s_churn": 0,
 * "s_tmax": 0,
 * "s_tmin": 0,
 * "s_noise": 1,
 * "override_settings": {},
 * "override_settings_restore_afterwards": true,
 * "script_args": [],
 * "sampler_index": "Euler",
 * "script_name": "string",
 * "send_images": true,
 * "save_images": false,
 * "alwayson_scripts": {}
 * }
 */

// -----------

/**
 {
 "prompt": "",
 "negative_prompt": "",
 "styles": [
 "string"
 ],
 "seed": -1,
 "subseed": -1,
 "subseed_strength": 0,
 "seed_resize_from_h": -1,
 "seed_resize_from_w": -1,
 "sampler_name": "string",
 "batch_size": 1,
 "n_iter": 1,
 "steps": 50,
 "cfg_scale": 7,
 "width": 512,
 "height": 512,
 "restore_faces": true,
 "tiling": true,
 "do_not_save_samples": false,
 "do_not_save_grid": false,
 "eta": 0,
 "denoising_strength": 0,
 "s_min_uncond": 0,
 "s_churn": 0,
 "s_tmax": 0,
 "s_tmin": 0,
 "s_noise": 0,
 "override_settings": {},
 "override_settings_restore_afterwards": true,
 "refiner_checkpoint": "string",
 "refiner_switch_at": 0,
 "disable_extra_networks": false,
 "comments": {},
 "enable_hr": false,
 "firstphase_width": 0,
 "firstphase_height": 0,
 "hr_scale": 2,
 "hr_upscaler": "string",
 "hr_second_pass_steps": 0,
 "hr_resize_x": 0,
 "hr_resize_y": 0,
 "hr_checkpoint_name": "string",
 "hr_sampler_name": "string",
 "hr_prompt": "",
 "hr_negative_prompt": "",
 "sampler_index": "Euler",
 "script_name": "string",
 "script_args": [],
 "send_images": true,
 "save_images": false,
 "alwayson_scripts": {}
 }

 */