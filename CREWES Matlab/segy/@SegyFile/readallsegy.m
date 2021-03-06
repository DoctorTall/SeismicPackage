function segyobj=readallsegy(segyobj,filein,varargin)
% SGY_readallsegy(filein)
%
%
% NOTE: This SOFTWARE may be used by any individual or corporation for any purpose
% with the exception of re-selling or re-distributing the SOFTWARE.
% By using this software, you are agreeing to the terms detailed in this software's
% Matlab source file.

% BEGIN TERMS OF USE LICENSE
%
% This SOFTWARE is maintained by the CREWES Project at the Department
% of Geology and Geophysics of the University of Calgary, Calgary,
% Alberta, Canada.  The copyright and ownership is jointly held by
% its 'AUTHOR' (identified above) and the CREWES Project.  The CREWES
% project may be contacted via email at:  crewesinfo@crewes.org
%
% The term 'SOFTWARE' refers to the Matlab source code, translations to
% any other computer language, or object code
%
% Terms of use of this SOFTWARE
%
% 1) This SOFTWARE may be used by any individual or corporation for any purpose
%    with the exception of re-selling or re-distributing the SOFTWARE.
%
% 2) The AUTHOR and CREWES must be acknowledged in any resulting publications or
%    presentations
%
% 3) This SOFTWARE is provided "as is" with no warranty of any kind
%    either expressed or implied. CREWES makes no warranties or representation
%    as to its accuracy, completeness, or fitness for any purpose. CREWES
%    is under no obligation to provide support of any kind for this SOFTWARE.
%
% 4) CREWES periodically adds, changes, improves or updates this SOFTWARE without
%    notice. New versions will be made available at www.crewes.org .
%
% 5) Use this SOFTWARE at your own risk.
%
% END TERMS OF USE LICENSE

% SGY_readallsegy reads in the entire SEG-Y File 
name={};
value={};
 for i = 1:2:length(varargin)
                name = varargin{i};
                value = varargin{i+1};
 end

segyobj.textheader=TextHeader(filein);
segyobj.binaryheader=BinaryHeader(filein);

%test to see if there is an extended header
fseek(segyobj.binaryheader.fid,3504,'bof');
numofext=fread(segyobj.binaryheader.fid,1,'uint16');
trstat=3600;
if numofext
    if numofext==-1
        numofext=500;
    end
    screensz=get(0,'ScreenSize');
    for n=1:numofext
        extheadtmp=TextHeader(filein,'txthoffset',num2str(trstat));
        response=listdlg('PromptString','Does this appear to be a somewhat legible Extended Header?',...
            'ListString',extheadtmp.header,'Name','Is This An Extended Header?',...
            'OkString','Yes','CancelString','No','SelectionMode','Single',...
            'ListSize',[.5*screensz(3),.5*screensz(4)]);
        if isempty(response)
            break
        else
            if response
                segyobj.extendedheader{n,1}=TextHeader(filein,'txthoffset',num2str(trstat));
                trstat=trstat+3200;
            else
                break;
            end
        end
        
    end
    
end
trch=Trace(filein,'hdroffset',num2str(trstat),'extendedheaderflag','no','new','1');
if any(strcmpi(name,'traces'))
    segyobj.trace=trch;
else
segyobj.trace=trch.getTraces();   
end

end