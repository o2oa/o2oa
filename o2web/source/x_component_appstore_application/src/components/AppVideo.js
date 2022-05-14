export default function AppVideo(props){
    if (props.data){
        const video = (props.data.video) ? <video controls autoplay={props.playVideo}><source src={props.data.video} /></video> : '';
        return (
            <div className="application-content-area">
                <div className="application-content-video">
                    {video}
                </div>
            </div>
        );
    }
    return '';
}