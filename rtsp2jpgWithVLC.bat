:begin
@ECHO OFF
ECHO.

SETLOCAL ENABLEDELAYEDEXPANSION

echo 0
pause


:: Display the results:
@set now=%SortDate%_%SortTime%
set now=%date%_%time%
set now=%now%
echo %now%
echo 1
pause

:: Proceed with RTSP capture
mkdir E:\TimeLapse\output5\Mpix%now%

echo 2
pause

@C:\Program Files (x86)\VideoLAN\VLC\vlc.exe
c:\programs\VideoLAN\VLC\vlc.exe rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov –video-filter=scene –scene-prefix=img- –scene-format=jpg –scene-path=e:\TimeLapse\Output5\Mpix%now% –scene-ratio 120 –sout-x264-lookahead=10 –sout-x264-tune=stillimage –vout=dummy –dummy-quiet –run-time 43200 vlc://quit
ENDLOCAL
echo 3
pause
goto begin