function formatTimeRange(startTime, endTime) {
    const formatDate = (date, includeTime = true) => {
        const [year, month, day, hours, minutes] = [
            date.getFullYear(),
            String(date.getMonth() + 1).padStart(2, '0'),
            String(date.getDate()).padStart(2, '0'),
            String(date.getHours()).padStart(2, '0'),
            String(date.getMinutes()).padStart(2, '0')
        ];
        return includeTime ? `${year}年${month}月${day}日 ${hours}:${minutes}` : `${year}年${month}月${day}日`;
    };

    const start = new Date(startTime);
    const end = new Date(endTime);

    return start.toDateString() === end.toDateString()
        ? `${formatDate(start)} - ${end.getHours()}:${String(end.getMinutes()).padStart(2, '0')}`
        : `${formatDate(start)} - ${formatDate(end)}`;
}

