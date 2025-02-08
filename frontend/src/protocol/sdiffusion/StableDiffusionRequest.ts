import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class StableDiffusionRequest {
    sampler_name: string = '';
    prompt: string = '';
    negative_prompt: string = '';
    seed: number = 0;
    steps: number = 0;
    batch_size: number = 0;
    width: number = 0;
    height: number = 0;
    cfg_scale: number = 0;
    enable_hr: boolean = false;
    hr_scale: number = 0;
    denoising_strength: number = 0;
    hr_upscaler: string = '';
}

export class StableDiffusionRequestRegistration implements IProtocolRegistration<StableDiffusionRequest> {
    protocolId(): number {
        return 1000;
    }

    write(buffer: IByteBuffer, packet: StableDiffusionRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.batch_size);
        buffer.writeInt(packet.cfg_scale);
        buffer.writeDouble(packet.denoising_strength);
        buffer.writeBool(packet.enable_hr);
        buffer.writeInt(packet.height);
        buffer.writeInt(packet.hr_scale);
        buffer.writeString(packet.hr_upscaler);
        buffer.writeString(packet.negative_prompt);
        buffer.writeString(packet.prompt);
        buffer.writeString(packet.sampler_name);
        buffer.writeLong(packet.seed);
        buffer.writeInt(packet.steps);
        buffer.writeInt(packet.width);
    }

    read(buffer: IByteBuffer): StableDiffusionRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new StableDiffusionRequest();
        const result0 = buffer.readInt();
        packet.batch_size = result0;
        const result1 = buffer.readInt();
        packet.cfg_scale = result1;
        const result2 = buffer.readDouble();
        packet.denoising_strength = result2;
        const result3 = buffer.readBool(); 
        packet.enable_hr = result3;
        const result4 = buffer.readInt();
        packet.height = result4;
        const result5 = buffer.readInt();
        packet.hr_scale = result5;
        const result6 = buffer.readString();
        packet.hr_upscaler = result6;
        const result7 = buffer.readString();
        packet.negative_prompt = result7;
        const result8 = buffer.readString();
        packet.prompt = result8;
        const result9 = buffer.readString();
        packet.sampler_name = result9;
        const result10 = buffer.readLong();
        packet.seed = result10;
        const result11 = buffer.readInt();
        packet.steps = result11;
        const result12 = buffer.readInt();
        packet.width = result12;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default StableDiffusionRequest;