import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import StableDiffusionParameters from './StableDiffusionParameters';


class StableDiffusionResponse {
    images: Array<string> = [];
    parameters: StableDiffusionParameters | null = null;
}

export class StableDiffusionResponseRegistration implements IProtocolRegistration<StableDiffusionResponse> {
    protocolId(): number {
        return 1001;
    }

    write(buffer: IByteBuffer, packet: StableDiffusionResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeStringList(packet.images);
        buffer.writePacket(packet.parameters, 1002);
    }

    read(buffer: IByteBuffer): StableDiffusionResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new StableDiffusionResponse();
        const list0 = buffer.readStringList();
        packet.images = list0;
        const result1 = buffer.readPacket(1002);
        packet.parameters = result1;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default StableDiffusionResponse;