# From src/version.h:#define OCTAVE_API_VERSION
%define octave_api api-v37
%define java_version  1.2.7

Name:           octave
Version:        3.2.2
Release:        1%{?dist}
Summary:        A high-level language for numerical computations
Epoch:          6

Group:          Applications/Engineering
License:        GPLv3+
Source0:         ftp://ftp.octave.org/pub/octave/octave-%{version}.tar.gz
Source1:         install-oct-java-%{java_version}.tar.gz
Source2:         java-%{java_version}.tar.gz
URL:            http://www.octave.org
Requires:       less info texinfo 
#Requires:       gnuplot less info texinfo 
Requires(post): /sbin/install-info
Requires(postun): /sbin/ldconfig
Requires(post): /sbin/ldconfig
Requires(preun): /sbin/install-info
#BuildRequires:  bison flex less tetex gcc-gfortran lapack-devel blas-devel
#BuildRequires:  ncurses-devel zlib-devel hdf5-devel texinfo qhull-devel
#BuildRequires:  readline-devel glibc-devel fftw-devel gperf ghostscript
#BuildRequires:  curl-devel pcre-devel
#BuildRequires:  suitesparse-devel glpk-devel gnuplot desktop-file-utils
Provides:       octave(api) = %{octave_api}
Prefix:        /usr/local/octave-3.2.2

BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

%define _prefix  %{prefix}
%define _libdir  %{prefix}/lib
%define _bindir  %{prefix}/bin
%define _datadir %{prefix}/share
%define _mandir %{prefix}/share/man
%define _infodir %{prefix}/share/info
%define _libexecdir %{prefix}/libexec
%define _includedir %{prefix}/include

%description
GNU Octave is a high-level language, primarily intended for numerical
computations. It provides a convenient command line interface for
solving linear and nonlinear problems numerically, and for performing
other numerical experiments using a language that is mostly compatible
with Matlab. It may also be used as a batch-oriented language. Octave
has extensive tools for solving common numerical linear algebra
problems, finding the roots of nonlinear equations, integrating
ordinary functions, manipulating polynomials, and integrating ordinary
differential and differential-algebraic equations. It is easily
extensible and customizable via user-defined functions written in
Octave's own language, or using dynamically loaded modules written in
C++, C, Fortran, or other languages.


%package devel
Summary:        Development headers and files for Octave
Group:          Development/Libraries
Requires:       %{name} = %{epoch}:%{version}-%{release}
#Requires:       readline-devel fftw-devel hdf5-devel zlib-devel
#Requires:       lapack-devel blas-devel gcc-c++ gcc-gfortran

%description devel
The octave-devel package contains files needed for developing
applications which use GNU Octave.


%prep
%setup -q
# Check that octave_api is set correctly
if ! grep -q '^#define OCTAVE_API_VERSION "%{octave_api}"' src/version.h
then
  echo "octave_api variable in spec does not match src/version.h"
  exit 1
fi

gzip -dc %{_topdir}/SOURCES/install-oct-java-%{java_version}.tar.gz | tar -xvvf -
# patch for sh arch
#%patch1 -p1 -b .sh-arch
# patch for gcc 4.4
#%patch2 -p1 -b .gcc44
cp -f %{_topdir}/SOURCES/java-%{java_version}.tar.gz ./

%build
%define enable64 no
export LIBS="-lpthread"
export LD_LIBRARY_PATH="/usr/lib:/lib:$LD_LIBRARY_PATH"
export PATH="/usr/bin:/bin:/usr/lib64/qt-3.3/bin:/usr/local/bin:/usr/X11R6/bin:$PATH"
export CPPFLAGS="-DH5_USE_16_API"
export FFLAGS="-0 -mieee-fp"
./configure --enable-shared \
--prefix=%{_prefix} \
--exec-prefix=%{_prefix} \
--disable-static \
F77=gfortran \
--without-blas \
--without-ccolamd \
--without-colamd \
--without-cholmod \
--without-cxsparse \
--without-umfpack \
--without-fftw \
--without-glpk \
--without-hdf5 \
--without-zlib \
--without-lapack \
--without-arpack \
--without-qrupdate \
--without-framework-opengl \
--without-framework-carbon \
--enable-doc=yes
make
#make %{?_smp_mflags} OCTAVE_RELEASE="Fedora %{version}-%{release}"
echo "****************** COMPLETE! - make ******************************************"

%install
rm -rf $RPM_BUILD_ROOT
make install DESTDIR=$RPM_BUILD_ROOT
rm -f ${RPM_BUILD_ROOT}%{_infodir}/dir
#cp -f %{_topdir}/SOURCES/install-oct-java.sh $RPM_BUILD_ROOT%{_bindir}/
#rm -f ${RPM_BUILD_ROOT}%{prefix}/share/info/dir
#rm -f ${RPM_BUILD_ROOT}%{_infodir}/dir
echo ">>>>>>>>>>1. INSTALL - rm  <<<<<<<"
# Make library links
mkdir -p $RPM_BUILD_ROOT/etc/ld.so.conf.d
echo "%{_libdir}/octave-%{version}" > $RPM_BUILD_ROOT/etc/ld.so.conf.d/octave-%{_arch}.conf
#echo "%{_libdir}/octave-%{version}" > $RPM_BUILD_ROOT/etc/ld.so.conf.d/octave-%{_arch}.conf
#echo "%{prefix}/lib/octave-%{version}" > $RPM_BUILD_ROOT/etc/ld.so.conf.d/octave-%{_arch}.conf
echo ">>>>>>>>>>2. INSTALL - mkdir  <<<<<<<"
# Remove RPM_BUILD_ROOT from ls-R files
perl -pi -e "s,$RPM_BUILD_ROOT,," $RPM_BUILD_ROOT%{_libexecdir}/%{name}/ls-R
perl -pi -e "s,$RPM_BUILD_ROOT,," $RPM_BUILD_ROOT%{_datadir}/%{name}/ls-R

#perl -pi -e "s,$RPM_BUILD_ROOT,," $RPM_BUILD_ROOT%{prefix}/libexec/%{name}/ls-R
#perl -pi -e "s,$RPM_BUILD_ROOT,," $RPM_BUILD_ROOT%{prefix}/share/%{name}/ls-R

echo ">>>>>>>>>>3. INSTALL - perl step  <<<<<<<"
# Clean doc directory
pushd doc
  make distclean
  rm -f *.in */*.in */*.cc refcard/*.tex
popd
echo ">>>>>>>>>>4. INSTALL - pushd & popd  <<<<<<<"
# Create desktop file
rm $RPM_BUILD_ROOT%{_datadir}/applications/www.octave.org-octave.desktop
#rm $RPM_BUILD_ROOT%{prefix}/share/applications/www.octave.org-octave.desktop
desktop-file-install --vendor fedora --add-category X-Fedora --remove-category Development \
	--dir $RPM_BUILD_ROOT%{_datadir}/applications examples/octave.desktop

#desktop-file-install --vendor fedora --add-category X-Fedora --remove-category Development \
#	--dir $RPM_BUILD_ROOT%{prefix}/share/applications examples/octave.desktop
#echo ">>>>>>>>>>5. INSTALL - create desktop  <<<<<<<"
# Create directories for add-on packages

HOST_TYPE=`$RPM_BUILD_ROOT%{_bindir}/octave-config -p CANONICAL_HOST_TYPE`
mkdir -p $RPM_BUILD_ROOT%{_libexecdir}/%{name}/site/oct/%{octave_api}/$HOST_TYPE
mkdir -p $RPM_BUILD_ROOT%{_libexecdir}/%{name}/site/oct/$HOST_TYPE
mkdir -p $RPM_BUILD_ROOT%{_datadir}/%{name}/packages

### Yee modified
#HOST_TYPE=`$RPM_BUILD_ROOT%{prefix}/bin/octave-config -p CANONICAL_HOST_TYPE`
#mkdir -p $RPM_BUILD_ROOT%{prefix}/libexec/%{name}/site/oct/%{octave_api}/$HOST_TYPE
#mkdir -p $RPM_BUILD_ROOT%{prefix}/libexec/%{name}/site/oct/$HOST_TYPE
#mkdir -p $RPM_BUILD_ROOT%{prefix}/share/%{name}/packages

#touch $RPM_BUILD_ROOT%{prefix}/share/%{name}/octave_packages
touch $RPM_BUILD_ROOT%{_datadir}/%{name}/octave_packages
echo ">>>>>>>>>> 6. INSTALL - create add-on  <<<<<<<"

# Make an octave symbolic link in /usr/local/bin
mkdir -p $RPM_BUILD_ROOT/usr/local/bin
touch $RPM_BUILD_ROOT/usr/local/bin/octave
touch $RPM_BUILD_ROOT/usr/local/bin/mkoctfile
## Install install scripts and java-1.2.7.tar.gz
install -c -m 755 install-oct-java.sh $RPM_BUILD_ROOT%{_bindir}/
install -c -m 755 install-octave-link.sh $RPM_BUILD_ROOT%{_bindir}/
install -c -m 755 java-%{java_version}.tar.gz $RPM_BUILD_ROOT%{_datadir}
%clean
cp -rf $RPM_BUILD_ROOT %{_topdir}/BUILDROOT/
rm -rf $RPM_BUILD_ROOT

%post
echo "%{_libdir}/octave-%{version}" > $RPM_BUILD_ROOT/etc/ld.so.conf.d/octave-%{_arch}.conf
/sbin/ldconfig -f $RPM_BUILD_ROOT/etc/ld.so.conf.d/octave-%{_arch}.conf
/sbin/install-info --info-dir=%{_infodir} --section="Programming" \
	%{_infodir}/octave.info || :

cp -f %{_datadir}/java-%{java_version}.tar.gz /var/tmp/
#chmod uag+x %{_bindir}/install-oct-java.sh
#/sbin/install-info --info-dir=%{prefix}/share/info --section="Programming" \
#	%{prefix}/share/info/octave.info || :
## Execute script to make octave symbolic link
if [ -f /usr/local/bin/octave ] ; then
   rm /usr/local/bin/octave
fi
export OCTAVE_PROG=octave-%{version}
cd %{_bindir}
%{_bindir}/install-octave-link.sh
## Execute install-oct-java.sh script
%{_bindir}/install-oct-java.sh

echo "=============== FINISH  -  post ================================="
%preun
if [ "$1" = "0" ]; then
#   /sbin/install-info --delete --info-dir=%{prefix}/share/info %{prefix}/share/info/octave.info || :
   /sbin/install-info --delete --info-dir=%{_infodir} %{_infodir}/octave.info || :
fi
echo "+++++++++++++++++++ FINISH - preun +++++++++++++++++++++++++++ "

%postun -p /sbin/ldconfig
echo "+++++++++++++++++++ FINISH - postun +++++++++++++++++++++++++++ "

%files
%defattr(-,root,root,-)
#%docdir %{_datadir}/doc
#%{prefix}/share/doc
%doc COPYING NEWS* PROJECTS README README.Linux README.kpathsea ROADMAP
%doc SENDING-PATCHES emacs examples doc/interpreter/octave.p*
%doc doc/faq doc/interpreter/HTML doc/refcard
%{_bindir}/octave*
%{_bindir}/install-oct*.sh
%{_bindir}/mkoctfile*
#%{_bindir}/*

#%{prefix}/bin/octave*
%config(noreplace) /etc/ld.so.conf.d/*
%{_libdir}/octave*
#%{_libdir}/*
#%{prefix}/lib64/octave*
#%{prefix}/lib/octave*

#%{_datadir}/octave

#%{prefix}/share/octave
%ghost %{_datadir}/octave/octave_packages
#%ghost %{prefix}/share/octave/octave_packages
%{_libexecdir}/octave
#%{prefix}/libexec/octave
%{_mandir}/man*/octave*
#%{prefix}/share/man/man*/octave*
%{_infodir}/octave.info*
#%{prefix}/share/info/octave.info*
#%{_datadir}/applications/*
%{_datadir}/*
%{_includedir}/*
#%{prefix}/share/applications/*
#/usr/share/doc/*
/usr/local/bin/

%files devel
%defattr(-,root,root,-)
%doc doc/liboctave
%{_bindir}/mkoctfile*
%{_includedir}/octave-%{version}
%{_mandir}/man*/mkoctfile*
/usr/share/doc/*
###
#%{prefix}/bin/mkoctfile*
#%{prefix}/include/octave-%{version}
#%{prefix}/share/man/man*/mkoctfile*


%changelog
* Thu Mar 24 2011 Charles Yee <yee@ll.mit.edu> - 1:3.2.2-1
- First cut to make rpm relocatable

