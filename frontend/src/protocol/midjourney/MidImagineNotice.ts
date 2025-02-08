import IByteBuffer from '../IByteBuffer';
import IProtocolRegistration from '../IProtocolRegistration';


class MidImagineNotice {
    // provider为加入到了providers队列，consumer为开始消费任务，create为创建消息，update为更新消息，complete为创建完成，stop为发生错误停止生成，expire图片过期
    type: string = '';
    nonce: string = '';
    content: string = '';
    // type为complete状态才有意义
    midjourneyId: number = 0;
    // 只有type为complete状态才能够访问图片
    imageUrl: string = '';
    imageUrlLow: string = '';
    imageUrlMiddle: string = '';
    imageUrlHigh: string = '';
    // 只有type为update状态才有意义
    progress: number = 0;
    // 只有type为complete状态才有意义
    reroll: boolean = false;
    upsample: boolean = false;
    upscale: boolean = false;
}

export class MidImagineNoticeRegistration implements IProtocolRegistration<MidImagineNotice> {
    protocolId(): number {
        return 802;
    }

    write(buffer: IByteBuffer, packet: MidImagineNotice | null) {
        if (packet === null) {
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(-1);
        buffer.writeString(packet.content);
        buffer.writeString(packet.imageUrl);
        buffer.writeString(packet.imageUrlHigh);
        buffer.writeString(packet.imageUrlLow);
        buffer.writeString(packet.imageUrlMiddle);
        buffer.writeLong(packet.midjourneyId);
        buffer.writeString(packet.nonce);
        buffer.writeInt(packet.progress);
        buffer.writeBool(packet.reroll);
        buffer.writeString(packet.type);
        buffer.writeBool(packet.upsample);
        buffer.writeBool(packet.upscale);
    }

    read(buffer: IByteBuffer): MidImagineNotice | null {
        const length = buffer.readInt();
        if (length === 0) {
            return null;
        }
        const beforeReadIndex = buffer.getReadOffset();
        const packet = new MidImagineNotice();
        const result0 = buffer.readString();
        packet.content = result0;
        const result1 = buffer.readString();
        packet.imageUrl = result1;
        const result2 = buffer.readString();
        packet.imageUrlHigh = result2;
        const result3 = buffer.readString();
        packet.imageUrlLow = result3;
        const result4 = buffer.readString();
        packet.imageUrlMiddle = result4;
        const result5 = buffer.readLong();
        packet.midjourneyId = result5;
        const result6 = buffer.readString();
        packet.nonce = result6;
        const result7 = buffer.readInt();
        packet.progress = result7;
        const result8 = buffer.readBool(); 
        packet.reroll = result8;
        const result9 = buffer.readString();
        packet.type = result9;
        const result10 = buffer.readBool(); 
        packet.upsample = result10;
        const result11 = buffer.readBool(); 
        packet.upscale = result11;
        if (length > 0) {
            buffer.setReadOffset(beforeReadIndex + length);
        }
        return packet;
    }
}

export default MidImagineNotice;