import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class StableDiffusionParameters {
    prompt: string = '';
    sampler_index: string = '';
    seed: number = 0;
    steps: number = 0;
    cfg_scale: number = 0;
    batch_size: number = 0;
    width: number = 0;
    height: number = 0;
    restore_faces: boolean = false;
    tiling: boolean = false;
}

export class StableDiffusionParametersRegistration implements IProtocolRegistration<StableDiffusionParameters> {
    protocolId(): number {
        return 1002;
    }

    write(buffer: IByteBuffer, packet: StableDiffusionParameters | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.batch_size);
        buffer.writeInt(packet.cfg_scale);
        buffer.writeInt(packet.height);
        buffer.writeString(packet.prompt);
        buffer.writeBool(packet.restore_faces);
        buffer.writeString(packet.sampler_index);
        buffer.writeInt(packet.seed);
        buffer.writeInt(packet.steps);
        buffer.writeBool(packet.tiling);
        buffer.writeInt(packet.width);
    }

    read(buffer: IByteBuffer): StableDiffusionParameters | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new StableDiffusionParameters();
        const result0 = buffer.readInt();
        packet.batch_size = result0;
        const result1 = buffer.readInt();
        packet.cfg_scale = result1;
        const result2 = buffer.readInt();
        packet.height = result2;
        const result3 = buffer.readString();
        packet.prompt = result3;
        const result4 = buffer.readBool(); 
        packet.restore_faces = result4;
        const result5 = buffer.readString();
        packet.sampler_index = result5;
        const result6 = buffer.readInt();
        packet.seed = result6;
        const result7 = buffer.readInt();
        packet.steps = result7;
        const result8 = buffer.readBool(); 
        packet.tiling = result8;
        const result9 = buffer.readInt();
        packet.width = result9;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default StableDiffusionParameters;