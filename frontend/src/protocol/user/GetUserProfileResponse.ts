import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import User from './User';


class GetUserProfileResponse {
    user: User | null = null;
}

export class GetUserProfileResponseRegistration implements IProtocolRegistration<GetUserProfileResponse> {
    protocolId(): number {
        return 755;
    }

    write(buffer: IByteBuffer, packet: GetUserProfileResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.user, 750);
    }

    read(buffer: IByteBuffer): GetUserProfileResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new GetUserProfileResponse();
        const result0 = buffer.readPacket(750);
        packet.user = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default GetUserProfileResponse;