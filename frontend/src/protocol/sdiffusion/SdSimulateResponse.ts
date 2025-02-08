import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class SdSimulateResponse {
    nonce: number = 0;
    costTime: number = 0;
    enPrompt: string = '';
}

export class SdSimulateResponseRegistration implements IProtocolRegistration<SdSimulateResponse> {
    protocolId(): number {
        return 1053;
    }

    write(buffer: IByteBuffer, packet: SdSimulateResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeLong(packet.costTime);
        buffer.writeString(packet.enPrompt);
        buffer.writeLong(packet.nonce);
    }

    read(buffer: IByteBuffer): SdSimulateResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new SdSimulateResponse();
        const result0 = buffer.readLong();
        packet.costTime = result0;
        const result1 = buffer.readString();
        packet.enPrompt = result1;
        const result2 = buffer.readLong();
        packet.nonce = result2;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default SdSimulateResponse;