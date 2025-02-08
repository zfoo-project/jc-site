import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';
import User from './User';


class UpdateUserProfileResponse {
    user: User | null = null;
}

export class UpdateUserProfileResponseRegistration implements IProtocolRegistration<UpdateUserProfileResponse> {
    protocolId(): number {
        return 753;
    }

    write(buffer: IByteBuffer, packet: UpdateUserProfileResponse | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writePacket(packet.user, 750);
    }

    read(buffer: IByteBuffer): UpdateUserProfileResponse | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new UpdateUserProfileResponse();
        const result0 = buffer.readPacket(750);
        packet.user = result0;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default UpdateUserProfileResponse;