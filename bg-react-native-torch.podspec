# bg-react-native-torch.podspec

require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "bg-react-native-torch"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  bg-react-native-torch
                   DESC
  s.homepage     = "https://github.com/KGajraj/bg-react-native-torch"
  # brief license entry:
  s.license      = "MIT"
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "Kieran Gajraj" => "Kieran.Gajraj@centrica.com" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/KGajraj/bg-react-native-torch.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,c,cc,cpp,m,mm,swift}"
  s.requires_arc = true

  s.dependency "React"
  # ...
  # s.dependency "..."
end

