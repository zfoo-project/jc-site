import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class SdSimulateRequest {
    nonce: number = 0;
    prompt: string = '';
    negativePrompt: string = '';
    steps: number = 0;
    batchSize: number = 0;
    // 图片的风格，0->二次元
    style: number = 0;
    // 图片的尺寸，0->768*768，1->768*1024
    dimension: number = 0;
    ignores: Array<number> = [];
}

export class SdSimulateRequestRegistration implements IProtocolRegistration<SdSimulateRequest> {
    protocolId(): number {
        return 1050;
    }

    write(buffer: IByteBuffer, packet: SdSimulateRequest | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeInt(packet.batchSize);
        buffer.writeInt(packet.dimension);
        buffer.writeLongList(packet.ignores);
        buffer.writeString(packet.negativePrompt);
        buffer.writeLong(packet.nonce);
        buffer.writeString(packet.prompt);
        buffer.writeInt(packet.steps);
        buffer.writeInt(packet.style);
    }

    read(buffer: IByteBuffer): SdSimulateRequest | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new SdSimulateRequest();
        const result0 = buffer.readInt();
        packet.batchSize = result0;
        const result1 = buffer.readInt();
        packet.dimension = result1;
        const list2 = buffer.readLongList();
        packet.ignores = list2;
        const result3 = buffer.readString();
        packet.negativePrompt = result3;
        const result4 = buffer.readLong();
        packet.nonce = result4;
        const result5 = buffer.readString();
        packet.prompt = result5;
        const result6 = buffer.readInt();
        packet.steps = result6;
        const result7 = buffer.readInt();
        packet.style = result7;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default SdSimulateRequest;